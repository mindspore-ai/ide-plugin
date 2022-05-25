/*
 * Copyright 2021-2022 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindspore.ide.toolkit.demo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mindspore.ide.toolkit.common.beans.NormalInfoConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * demo search method
 *
 * @since 2022-04-18
 */
public enum GuessYouLike {
    INSTANCE;

    private List<GuessItem> pageListExpert = new ArrayList<>();
    private List<GuessItem> pageListFresh = new ArrayList<>();
    private List<GuessItem> pageListTransfer = new ArrayList<>();

    private String pageExpert1 = NormalInfoConstants.EXPERT_1;
    private String pageExpert2 = NormalInfoConstants.EXPERT_2;
    private String pageExpert3 = NormalInfoConstants.EXPERT_3;

    private String pageFresh1 = NormalInfoConstants.FRESH_1;
    private String pageFresh2 = NormalInfoConstants.FRESH_2;
    private String pageFresh3 = NormalInfoConstants.FRESH_3;

    private String pathTransfer1 = NormalInfoConstants.TRANSFER_1;
    private String pathTransfer2 = NormalInfoConstants.TRANSFER_2;
    private Gson gson = new Gson();

    GuessYouLike() {
        intiExpertPages();
        intiFreshPages();
        intiTransferPages();
    }

    /**
     * et next page
     *
     * @param who user type
     * @return page result
     */
    public List<GuessItem> getNextPage(UserType who) {
        if (who == UserType.MASTER) {
            return pageListExpert;
        } else if (who == UserType.NEWBIE) {
            return pageListFresh;
        } else if (who == UserType.TRANSFER) {
            return pageListTransfer;
        } else {
            return new ArrayList<>(0);
        }
    }

    private void intiExpertPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add(pageExpert1);
        pageList.add(pageExpert2);
        pageList.add(pageExpert3);
        loadData(pageList, pageListExpert);
    }

    private void intiFreshPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add(pageFresh1);
        pageList.add(pageFresh2);
        pageList.add(pageFresh3);
        loadData(pageList, pageListFresh);
    }

    private void intiTransferPages() {
        List<String> pageList = new ArrayList<>();
        pageList.add(pathTransfer1);
        pageList.add(pathTransfer2);
        loadData(pageList, pageListTransfer);
    }

    private void loadData(List<String> pathList, List<GuessItem> pageListTransfer) {
        for (String path : pathList) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(GuessYouLike.class.getResourceAsStream(path), "utf8")
            )) {
                List<GuessItem> pageRaw = new ArrayList<>();
                JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
                for (JsonElement single : array) {
                    GuessItem res = gson.fromJson(single, GuessItem.class);
                    pageRaw.add(res);
                }
                pageListTransfer.addAll(pageRaw);
            } catch (IOException exceptionIo) {
                pageListTransfer.addAll(null);
            }
        }
    }
}