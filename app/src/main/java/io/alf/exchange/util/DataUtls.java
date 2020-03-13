package io.alf.exchange.util;

import java.util.ArrayList;
import java.util.List;


public class DataUtls {

    public static List<String> mlist() {
        List<String> mlist = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            mlist.add(String.valueOf(i));
        }
        return mlist;
    }

    public static List<String> mlist1() {
        List<String> mlist = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mlist.add(String.valueOf(i));
        }
        return mlist;
    }

    public static List showTradeList(int size) {
        List list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(null);
        }
        return list;
    }

    public static List showTradeList1() {
        List mlist = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            mlist.add(null);
        }
        return mlist;
    }
}
