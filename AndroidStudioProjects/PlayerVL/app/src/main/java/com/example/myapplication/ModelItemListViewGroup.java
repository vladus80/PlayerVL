package com.example.myapplication;

class ModelItemListViewGroup {

        private String mName;
        private Long mCountChannels;

public ModelItemListViewGroup(String name, Long description) {
        this.mName = name;
        this.mCountChannels = description;
        }

public String getName() {
        return mName;
        }

public void setName(String name) {
        mName = name;
        }

public Long getDescription() {
        return mCountChannels;
        }

public void setDescription(Long description) {
        mCountChannels = description;
        }
}

