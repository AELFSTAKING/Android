package io.cex.exchange.kotlin.valuation

import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.alf.exchange.App
import io.alf.exchange.R
import io.cex.exchange.kotlin.coreutil.defaultLegalCurrency
import io.cex.exchange.kotlin.coreutil.getSpValue
import io.cex.exchange.kotlin.coreutil.isDefaultLegalCurrency
import io.cex.exchange.kotlin.coreutil.putSpValue
import io.tick.base.mvp.IView
import io.tick.base.mvp.Presenter
import org.greenrobot.eventbus.EventBus


class ValuationPresenter : Presenter<ValuationView>() {

    companion object {
        private const val KEY_FULL_FIAT_LIST = "valuationList"
        private const val KEY_SELECTED_FIAT = "selectedValuation"
        private const val KEY_DEFAULT_FIAT = "defaultFiat"

        /**
         * 缓存当前使用的实体，避免频繁读取 sp
         */
        private var cachedEntity: ValuationEntity? = null

        /**
         * 获取当前估值币种实体。
         */
        fun currentValuation(): ValuationEntity {
            if (cachedEntity == null) {
                synchronized(this) {
                    val list = readLocalList()
                    val local = readLocalEntity()
                    //规则：
                    // 1. 不做设置时:
                    //    如果列表为空则使用 默认法币
                    //    列表不为空时优先使用 默认法币, 如果没有 默认法币 则使用第一项
                    // 2. 已经设置过：
                    //    如果列表为空，使用 默认法币 （需要通知外部！）
                    //    如果列表中包含原来的设置项则直接返回原来的设置项。
                    //    如果列表不为空且找不到原来的设置项，优先使用默认法币，否则使用第一项（需要通知外部！）
                    var forceChanged = false
                    cachedEntity = when {
                        local == null -> {
                            if (list.isEmpty()) {
                                defaultLegalCurrency()
                            } else {
                                var temp: ValuationEntity? = null
                                //在韩语区域，如果没有显式指定过计价币种，则默认韩元，如果列表中没有这个币种，则使用默认法币
                                val actualLang = App.getContext().getString(R.string.web_ua_language)
                                    .substring(0..1) //ko, en, etc
                                if (actualLang == "ko") {
                                    temp = list.firstOrNull {
                                        it.currencyCode.toUpperCase() == "KRW"
                                    }
                                }
                                if (temp == null) {
                                    temp = list.firstOrNull { isDefaultLegalCurrency(it.currencyCode) }
                                            ?: list[0]
                                }
                                temp
                            }
                        }
                        list.isEmpty() -> {
                            forceChanged = true
                            defaultLegalCurrency()
                        }
                        //TODO 考虑如何优化额外的一次遍历
                        list.any { it.currencyCode.equals(local.currencyCode, true) } -> list.first {
                            it.currencyCode.equals(local.currencyCode, true)
                        }
                        else -> {
                            forceChanged = true
                            list.firstOrNull { isDefaultLegalCurrency(it.currencyCode) } ?: list[0]
                        }
                    }

                    if (forceChanged) {
                        //删除不合法的缓存
                        putSpValue(App.getContext(), key = KEY_SELECTED_FIAT, value = "")
                        EventBus.getDefault().post(cachedEntity)
                    }
                }
            }
            return cachedEntity!!
        }


        /**
         * 保存估值币种列表，列表内容可以为空。
         */
        private fun saveValuationList(list: List<ValuationEntity>) {
            val json = Gson().toJson(list)
            putSpValue(App.getContext(), key = KEY_FULL_FIAT_LIST, value = json)
            //重置缓存
            cachedEntity = null
        }

        /**
         * 保存当前所用的估值实体类。
         */
        fun saveValuationEntity(selected: ValuationEntity) {
            cachedEntity = selected
            putSpValue(App.getContext(), key = KEY_SELECTED_FIAT, value = Gson().toJson(selected))
        }

        private fun saveDefaultFiat(default: ValuationEntity) {
            putSpValue(App.getContext(), key = KEY_DEFAULT_FIAT, value = Gson().toJson(default))
        }

        fun readDefaultCurrency(): ValuationEntity? {
            val localStr = getSpValue(App.getContext(), key = KEY_DEFAULT_FIAT, defaultVal = "")
            return if (localStr.isEmpty()) {
                null
            } else {
                Gson().fromJson(localStr, ValuationEntity::class.java)
            }
        }

        private fun readLocalEntity(): ValuationEntity? {
            val localStr = getSpValue(App.getContext(), key = KEY_SELECTED_FIAT, defaultVal = "")
            return if (localStr.isEmpty()) {
                null
            } else {
                Gson().fromJson(localStr, ValuationEntity::class.java)
            }
        }

        /**
         * 读取本地保存的估值币种列表，如果本地不存在则返回空列表。
         */
        internal fun readLocalList(): List<ValuationEntity> {
            val localListStr = getSpValue(App.getContext(), key = KEY_FULL_FIAT_LIST, defaultVal = "")
            return if (localListStr.isEmpty()) {
                emptyList()
            } else {
                Gson().fromJson(localListStr, object : TypeToken<List<ValuationEntity>>() {}.type)
            }
        }

    }

    fun onCreate(owner: LifecycleOwner) {

        val localList = readLocalList().toMutableList()
        //如果本地没有列表，则展示默认法币
        if (localList.isEmpty()) {
            localList.add(defaultLegalCurrency())
        }
        localList.firstOrNull {
            it.currencyCode.equals(currentValuation().currencyCode, true)
        }?.apply {
            isSelected = true
        }

        view.displayValuationList(localList)

        fetchValuationList()
    }

    /**
     * 拉取并保存最新的估值币种列表。接口数据成功返回后会自动重置缓存的估值实体，确保下次计算使用到的汇率是正确的。
     */
    fun fetchValuationList() {
        val params = hashMapOf<String, Any>()
/*        HttpUtils.postRequest(GET_VALUATION_LIST, view, params, JsonCallback<object>(){

        });*/
/*        APIFactory.getInstance()
            .getValuationList(ProgressSubscriber(object : SubscriberOnNextListener<ValuationEntityWrapper> {
                override fun onNext(wrapper: ValuationEntityWrapper?) {
                    if (wrapper?.exchangeRate == null) {
                        return
                    }
                    saveValuationList(wrapper.exchangeRate)
                    if (wrapper.defaultCurrency != null) {
                        saveDefaultFiat(wrapper.defaultCurrency)
                    }
                    if (wrapper.exchangeRate.isNotEmpty()) {
                        var hasSelection = false
                        wrapper.exchangeRate.firstOrNull {
                            it.currencyCode.equals(currentValuation().currencyCode, true)
                        }?.apply {
                            isSelected = true
                            hasSelection = true
                        }
                        if (!hasSelection) {
                            wrapper.exchangeRate[0].isSelected = true
                        }
                    }

                    iView?.displayValuationList(wrapper.exchangeRate)
                }

                override fun onError(code: Int, message: String?) {
                    //Empty. silent fail
                }
            }, mContext, false), params)*/
    }
}

interface ValuationView : IView {
    fun displayValuationList(list: List<ValuationEntity>)
}