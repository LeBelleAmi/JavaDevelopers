package com.lebelle.javadevelopers;

/**
 * This represents the java developers
 */

public class JavaDevelopers {

        /**parameter for username*/
        private String mUsername;

    /**parameter for fullname*/
    private String mProfileUrl;

        /**parameter for avatar*/
        private String mAvatar;


        /** parameter of the user's github page link*/
        private String mUrl;


        /** create a new word object.
         * @param username is the username of the java developer
         *                 @param profileUrl is the fullname of the java developer
         *                           @param avatar is the image of the java developer
         *
         * @param url is the website URL to find the page of the java developer
         *
         */


        public JavaDevelopers(String username, String profileUrl, String avatar,  String url){
            mUsername = username;
            mProfileUrl = profileUrl;
            mAvatar = avatar;
            mUrl = url;
        }

        /**get the username of the java developer*/
        public String getUsername (){
            return mUsername;
        }

    /**get the fullname of the java developer*/
    public String getProfileUrl (){
        return mProfileUrl;
    }


        /**get the image of the java developer*/
        public String getAvatar (){
            return mAvatar;
        }

        /** Returns the website URL to find the page of the java developer*/
        public String getUrl() {return mUrl;}

    }


