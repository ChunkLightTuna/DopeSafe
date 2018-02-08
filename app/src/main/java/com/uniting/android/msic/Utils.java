package com.uniting.android.msic;

import android.telephony.PhoneNumberUtils;

/**
 * Static stuff that should be defined in one place, but used in many places and whos implementation
 * may change.
 */

public class Utils {

    public static boolean validSMS(String number) {
        return PhoneNumberUtils.isWellFormedSmsAddress(number);
//        return PhoneNumberUtils.isGlobalPhoneNumber(number);
    }

}
