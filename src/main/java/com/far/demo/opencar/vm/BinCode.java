package com.far.demo.opencar.vm;

/*
 *@description:存储代码
 *@author mike/Fang.J
 *@date 2022/11/2 9:59
 */

import java.util.ArrayList;
import java.util.List;

public class BinCode {

    private List<Integer> codes = new ArrayList<>();

    public void loadBin(BinFile file){
        while (file.available() > 0) {
            int code = file.readInt();
            codes.add(code);
        }
    }

    public int get(int pc){
        return codes.get(pc);
    }


}
