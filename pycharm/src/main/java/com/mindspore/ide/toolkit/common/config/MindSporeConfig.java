package com.mindspore.ide.toolkit.common.config;

public class MindSporeConfig {

    SearchConfig search;

    RomaConfig roma;

    public SearchConfig getSearch() {
        return search.clone();
    }

    public void setSearch(SearchConfig search) {
        this.search = search.clone();
    }

    public RomaConfig getRoma() {
        return roma.clone();
    }

    public void setRoma(RomaConfig roma) {
        if(roma != null ){
            this.roma = roma.clone();
        }else{
            this.roma = null;
        }

    }
}
