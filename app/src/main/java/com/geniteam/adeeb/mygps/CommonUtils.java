package com.geniteam.adeeb.mygps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;

public class CommonUtils {


    public static boolean isNetworkAvailable(Context callingContext) {
        try {
            ConnectivityManager manager = (ConnectivityManager) callingContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnected();
            return isConnected && activeNetwork.getDetailedState() != NetworkInfo.DetailedState.VERIFYING_POOR_LINK;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLocationServicesAvailable(Context context) {
        int locationMode = 0;
        boolean isAvailable = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } else {
            String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isAvailable = !TextUtils.isEmpty(locationProviders);
        }

        return isAvailable;
    }

    public static void setWeight(LinearLayout layout, int weight) {
        try {
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) layout.getLayoutParams();
            params.weight=weight;
            layout.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmailValid(String mailid) {
        // TODO Auto-generated method stub

        return android.util.Patterns.EMAIL_ADDRESS.matcher(mailid).matches();
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static String covert(int roundOff, float value) {
        NumberFormat discountFormat = NumberFormat.getInstance();
        discountFormat.setMaximumFractionDigits(roundOff);
        return discountFormat.format(value);
    }

    public static String rgetEncodedBitmapString(Uri data) {
        try {
            //getting bitmap string
            if (data != null) {
                Runtime.getRuntime().gc();
                Bitmap bm = decodeFile(new File(data.toString().replace("file://", "")));

                if (bm != null) {
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 50,
                                byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String encodeImage = Base64.encodeToString(byteArray,
                                Base64.DEFAULT).toString();
                        bm.recycle();
                        Runtime.getRuntime().gc();
                        return encodeImage;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEncodedBitmapString(Bitmap bitmap) {
        try {
            //getting bitmap string
            if (bitmap != null) {
                Runtime.getRuntime().gc();
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30,
                            byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encodeImage = Base64.encodeToString(byteArray,
                            Base64.DEFAULT);
                    Runtime.getRuntime().gc();
                    return encodeImage;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap decodeFile(File f) throws OutOfMemoryError {
        try {
            // Decode image file with size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            int width = 500;
            int height = 500;

            // Find the correct scale value. It should be the power of 2.
            o.inSampleSize = calculateInSampleSize(o, width,
                    height);

            o.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, o);

			/* Exif transformation checking */

            Matrix matrix = new Matrix();

            int orientation = getImageOrientation(f);

            switch (orientation) {
                case 1:
                    break; // top left
                case 2:
                    matrix.postScale(-1, 1);
                    break; // top right
                case 3:
                    matrix.postRotate(180);
                    matrix.postScale((float) o.outWidth,
                            (float) o.outHeight);
                    break; // bottom right
                case 4:
                    matrix.postRotate(180);
                    matrix.postScale(-1, 1);
                    break; // bottom left
                case 5:
                    matrix.postRotate(90);
                    matrix.postScale(-1, 1);
                    break; // left top
                case 6:
                    matrix.postRotate(90);
                    break; // right top
                case 7:
                    matrix.postRotate(270);
                    matrix.postScale(-1, 1);
                    break; // right bottom
                case 8:
                    matrix.postRotate(270);

                    break; // left bottom
                default:
                    break; // Unknown
            }

            try {
                bm = Bitmap.createBitmap(bm, 0, 0, o.outWidth,
                        o.outHeight, matrix, true);
                return bm;
            } catch (OutOfMemoryError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            return null;
        }
        return null;
    }

    public static int getImageOrientation(File file) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(file.toString());
            return exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 1);
        } catch (IOException e) {
            return 0;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down
            // further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }


    public static void setBottomMargin(View view, int margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(0, margin, 0, 0);
        view.setLayoutParams(layoutParams);
    }

    public static String getLatitudeByCityID(int RegionId){

        if (RegionId == 1){

            return  "25.2048";
        }

        else if (RegionId ==2){

            return "25.3463";
        }

        else if (RegionId ==3){

            return "25.4052";
        }

        else if (RegionId ==4){
            return "25.5205";

        }

        else if (RegionId ==5){
            return "25.8007";

        }

        else if (RegionId ==6){
            return "25.4111";

        }

        else if (RegionId ==7){
            return "24.4539";

        }

        else if (RegionId ==8){
            return "24.1302";

        }

        return "0.0";

    }

    public static String getLongitudeByCityID(int RegionId){

        if (RegionId == 1){
            return  "55.2708";
        }

        else if (RegionId ==2){

            return  "55.4209";
        }

        else if (RegionId ==3){
            return  "55.5136";

        }

        else if (RegionId ==4){

            return "55.7134";
        }

        else if (RegionId ==5){
            return  "55.9762";

        }

        else if (RegionId ==6){

            return  "56.2482";
        }

        else if (RegionId ==7){
            return  "54.3773";

        }

        else if (RegionId ==8){

            return  "55.8023";
        }

        return "0.0";

    }

    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for(int i=0;i<number.length();i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }

}

