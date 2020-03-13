package io.alf.exchange;

public class Constant {
    //打开相机
    public final static int RC_ACTION_IMAGE_CAPTURE = 4002;
    //打开相册
    public final static int RC_ACTION_PICK = 4003;
    //获取二维码
    public static final int RC_GET_QRCODE = 4004;

    public static final String FILEPROVIDER_AUTHORITIES_VALUE = App.getContext().getPackageName() + ".fileProvider";


    // 1分钟
    public static final String MIN_1 = "60000";
    // 5分钟
    public static final String MIN_5 = "300000";
    // 15分钟
    public static final String MIN_15 = "900000";
    // 30分钟
    public static final String MIN_30 = "1800000";
    // 1小时
    public static final String HOUR_1 = "3600000";
    // 2小时
    public static final String HOUR_2 = "7200000";
    // 小时
    public static final String HOUR_4 = "14400000";
    // 小时
    public static final String HOUR_6 = "21600000";
    // 小时
    public static final String HOUR_12 = "43200000";
    // 日
    public static final String DAY_1 = "86400000";
    // 周
    public static final String WEEK_1 = "604800000";

    public static final String KOFO_ID = "KOFOrkk9LjEAYL3rDdmDSQHRRim9YKjs44JzzAHkdk7g112W";

    public static final String KOFO_PUBKEY =
            "02e3416ac241127a91cbc975c41cbdf423eb48623e4242f995c3aeb9c6e8fdcd47";

    public static final String KOFO_PRIKEY =
            "3060114d568d57da4c5bb65b501645baabe5f8ba986a292e1a1e9bc5b6b1e3df";
}
