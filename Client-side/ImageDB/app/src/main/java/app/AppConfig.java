package app;

/**
 * Declare the login and registration urls.
 */

public class AppConfig {

    //  ImageDB server Url
    public static String DEBUG_HOST_NAME = "http://imagedb";
    public static String URL_IMAGEDB = "http://SERVER_URL";

    //  API
    private static String IMAGEDB_API_SUFIX = "/API/";
    private static String S_LOGIN = "login.php";
    private static String S_REGISTER = "register.php";
    private static String S_IMAGEUPLOADER = "imageUploader.php";
    private static String S_GALLERY = "gallery.php";
    private static String S_TOP = "top.php";
    private static String S_POST = "post.php";


    // Server user login url
    public static String getLogin_Url() {
        return URL_IMAGEDB + IMAGEDB_API_SUFIX + S_LOGIN;
    }

    // Server user register url
    public static String getRegister_Url() {
        return URL_IMAGEDB + IMAGEDB_API_SUFIX + S_REGISTER;
    }

    // Server image uploader url
    public static String getImageUploader_Url() {
        return URL_IMAGEDB + IMAGEDB_API_SUFIX + S_IMAGEUPLOADER;
    }

    // Server user image gallery url
    public static String getGallery_Url() {
        return URL_IMAGEDB + IMAGEDB_API_SUFIX + S_GALLERY;
    }

    // Server image top url
    public static String getTop_Url() {
        return URL_IMAGEDB + IMAGEDB_API_SUFIX + S_TOP;
    }

    //  GalleryActivity Mode
    public static String GALLERY_MODE = "GALLERY_MODE";
    public static String GALLERY_MODE_GALLERY = "myGallery";
    public static String GALLERY_MODE_TOP = "top";

    //  Top image
    public static String TOP_IMAGE = "top_image";

    // Server post url
    public static String getPost_Url() {
        return URL_IMAGEDB + "/" + S_POST;
    }
}