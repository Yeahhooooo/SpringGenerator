package com.easyjavatool;

import com.easyjavatool.bean.TableInfo;
import com.easyjavatool.builder.*;

import java.util.List;

public class RunApplication {

    public static void main(String[] args) {
        List<TableInfo> tableInfoList = BuildTable.getTables();
        for (TableInfo tableInfo : tableInfoList) {
            BuildPO.execute(tableInfo);
            BuildQuery.execute(tableInfo);
            BuildMapper.execute(tableInfo);
            BuildMapperXML.execute(tableInfo);
            BuildService.execute(tableInfo);
            BuildServiceImpl.execute(tableInfo);
            BuildController.execute(tableInfo);
        }

        BuildBase.execute();
        BuildConfig.execute();
    }
}
