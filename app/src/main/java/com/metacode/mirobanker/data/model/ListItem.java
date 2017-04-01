package com.metacode.mirobanker.data.model;

/*
 * Copyright 2017 Sourabh Verma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ListItem {

    public static class Manager {
        public String mName = "";
        public Manager() {
            // for firebase
        }

        public String getName() {
            return mName;
        }

        public void setName(String mName) {
            this.mName = mName;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private String mName = "";
    private int mAge;
    private int mGender;
    private Manager mManager = new Manager();
    private String mImageUrl = "";
    private String mPhoneNumber = "";

    public ListItem() {
        // for firebase
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int mAge) {
        this.mAge = mAge;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int mGender) {
        this.mGender = mGender;
    }

    public Manager getManager() {
        return mManager;
    }

    public void setManager(Manager mManager) {
        this.mManager = mManager;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "mName='" + mName + '\'' +
                ", mAge=" + mAge +
                ", mGender=" + mGender +
                ", mManager=" + mManager +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                '}';
    }
}
