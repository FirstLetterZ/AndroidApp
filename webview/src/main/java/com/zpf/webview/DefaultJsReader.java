package com.zpf.webview;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DefaultJsReader {
    public static String assetsFileName = "bridge.js";

    public static String loadJsFromAssetsFile(Context context, boolean useDefault) {
        InputStream in = null;
        try {
            in = context.getAssets().open(assetsFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String jsFileString = null;
        if (in != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder sb = new StringBuilder();
                do {
                    line = bufferedReader.readLine();
                    if (line != null && !line.matches("^\\s*//.*")) {
                        sb.append(line);
                    }
                } while (line != null);
                bufferedReader.close();
                in.close();
                jsFileString = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        if (jsFileString == null && useDefault) {
            jsFileString = "function CallNative(name, body, callBack) {\n" +
                    "    if (!name) {\n" +
                    "        return;\n" +
                    "    }\n" +
                    "    var id = \"\";\n" +
                    "    var param;\n" +
                    "    if(!body){\n" +
                    "        param = \"\";\n" +
                    "    }else if(typeof body === 'string'){\n" +
                    "        param = body;\n" +
                    "    }else{\n" +
                    "        param = JSON.stringify(body);\n" +
                    "    }\n" +
                    "    if(callBack && typeof callBack === 'function'){\n" +
                    "        var date = new Date();\n" +
                    "        var id = date.toISOString();\n" +
                    "        var newAction = {\n" +
                    "            id: id,\n" +
                    "            callBack: callBack,\n" +
                    "        };\n" +
                    "        Native.actions.push(newAction);\n" +
                    "    }\n" +
                    "   return window.bridge.callNative(name,param,id);\n" +
                    "};\n" +
                    "function NativeCallBack(id, body) {\n" +
                    "    if (!id) {\n" +
                    "        return;\n" +
                    "    }\n" +
                    "    if (!Native.actions) {\n" +
                    "        return;\n" +
                    "    }\n" +
                    "    for (var index in Native.actions) {\n" +
                    "        var action = Native.actions[index];\n" +
                    "        if (action.id === id) {\n" +
                    "            var callBack = action.callBack;\n" +
                    "            if (callBack && typeof callBack === 'function') {\n" +
                    "                if (body) {\n" +
                    "                    callBack(body)\n" +
                    "                } else {\n" +
                    "                    callBack()\n" +
                    "                }\n" +
                    "            }\n" +
                    "            break;\n" +
                    "        }\n" +
                    "    }\n" +
                    "};\n" +
                    "if (!Native) {\n" +
                    "    var Native = window.Native ={\n" +
                    "        actions : [],\n" +
                    "        jsCall : CallNative\n" +
                    "    };\n" +
                    "    window.bridge.connect();\n" +
                    "}";
        }
        return jsFileString;
    }
}
