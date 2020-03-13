package io.alf.exchange.mvp.bean;

import java.util.List;

public class Favorites {
    private List<String> symbolList;

    public Favorites(List<String> symbolList) {
        this.symbolList = symbolList;
    }

    public List<String> getSymbolList() {
        return symbolList;
    }

    public void setSymbolList(List<String> symbolList) {
        this.symbolList = symbolList;
    }
}
