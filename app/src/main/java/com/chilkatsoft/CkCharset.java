/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkCharset {
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    protected CkCharset(long cPtr, boolean cMemoryOwn) {
        swigCMemOwn = cMemoryOwn;
        swigCPtr = cPtr;
    }

    public CkCharset() {
        this(chilkatJNI.new_CkCharset(), true);
    }

    protected static long getCPtr(CkCharset obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                chilkatJNI.delete_CkCharset(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    public void LastErrorXml(CkString str) {
        chilkatJNI.CkCharset_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public void LastErrorHtml(CkString str) {
        chilkatJNI.CkCharset_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public void LastErrorText(CkString str) {
        chilkatJNI.CkCharset_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public void get_AltToCharset(CkString str) {
        chilkatJNI.CkCharset_get_AltToCharset(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String altToCharset() {
        return chilkatJNI.CkCharset_altToCharset(swigCPtr, this);
    }

    public void put_AltToCharset(String newVal) {
        chilkatJNI.CkCharset_put_AltToCharset(swigCPtr, this, newVal);
    }

    public void get_DebugLogFilePath(CkString str) {
        chilkatJNI.CkCharset_get_DebugLogFilePath(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String debugLogFilePath() {
        return chilkatJNI.CkCharset_debugLogFilePath(swigCPtr, this);
    }

    public void put_DebugLogFilePath(String newVal) {
        chilkatJNI.CkCharset_put_DebugLogFilePath(swigCPtr, this, newVal);
    }

    public int get_ErrorAction() {
        return chilkatJNI.CkCharset_get_ErrorAction(swigCPtr, this);
    }

    public void put_ErrorAction(int newVal) {
        chilkatJNI.CkCharset_put_ErrorAction(swigCPtr, this, newVal);
    }

    public void get_FromCharset(CkString str) {
        chilkatJNI.CkCharset_get_FromCharset(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String fromCharset() {
        return chilkatJNI.CkCharset_fromCharset(swigCPtr, this);
    }

    public void put_FromCharset(String newVal) {
        chilkatJNI.CkCharset_put_FromCharset(swigCPtr, this, newVal);
    }

    public void get_LastErrorHtml(CkString str) {
        chilkatJNI.CkCharset_get_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String lastErrorHtml() {
        return chilkatJNI.CkCharset_lastErrorHtml(swigCPtr, this);
    }

    public void get_LastErrorText(CkString str) {
        chilkatJNI.CkCharset_get_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String lastErrorText() {
        return chilkatJNI.CkCharset_lastErrorText(swigCPtr, this);
    }

    public void get_LastErrorXml(CkString str) {
        chilkatJNI.CkCharset_get_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String lastErrorXml() {
        return chilkatJNI.CkCharset_lastErrorXml(swigCPtr, this);
    }

    public void get_LastInputAsHex(CkString str) {
        chilkatJNI.CkCharset_get_LastInputAsHex(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String lastInputAsHex() {
        return chilkatJNI.CkCharset_lastInputAsHex(swigCPtr, this);
    }

    public void get_LastInputAsQP(CkString str) {
        chilkatJNI.CkCharset_get_LastInputAsQP(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String lastInputAsQP() {
        return chilkatJNI.CkCharset_lastInputAsQP(swigCPtr, this);
    }

    public boolean get_LastMethodSuccess() {
        return chilkatJNI.CkCharset_get_LastMethodSuccess(swigCPtr, this);
    }

    public void put_LastMethodSuccess(boolean newVal) {
        chilkatJNI.CkCharset_put_LastMethodSuccess(swigCPtr, this, newVal);
    }

    public void get_LastOutputAsHex(CkString str) {
        chilkatJNI.CkCharset_get_LastOutputAsHex(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String lastOutputAsHex() {
        return chilkatJNI.CkCharset_lastOutputAsHex(swigCPtr, this);
    }

    public void get_LastOutputAsQP(CkString str) {
        chilkatJNI.CkCharset_get_LastOutputAsQP(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String lastOutputAsQP() {
        return chilkatJNI.CkCharset_lastOutputAsQP(swigCPtr, this);
    }

    public boolean get_SaveLast() {
        return chilkatJNI.CkCharset_get_SaveLast(swigCPtr, this);
    }

    public void put_SaveLast(boolean newVal) {
        chilkatJNI.CkCharset_put_SaveLast(swigCPtr, this, newVal);
    }

    public void get_ToCharset(CkString str) {
        chilkatJNI.CkCharset_get_ToCharset(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String toCharset() {
        return chilkatJNI.CkCharset_toCharset(swigCPtr, this);
    }

    public void put_ToCharset(String newVal) {
        chilkatJNI.CkCharset_put_ToCharset(swigCPtr, this, newVal);
    }

    public boolean get_VerboseLogging() {
        return chilkatJNI.CkCharset_get_VerboseLogging(swigCPtr, this);
    }

    public void put_VerboseLogging(boolean newVal) {
        chilkatJNI.CkCharset_put_VerboseLogging(swigCPtr, this, newVal);
    }

    public void get_Version(CkString str) {
        chilkatJNI.CkCharset_get_Version(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String version() {
        return chilkatJNI.CkCharset_version(swigCPtr, this);
    }

    public int CharsetToCodePage(String charsetName) {
        return chilkatJNI.CkCharset_CharsetToCodePage(swigCPtr, this, charsetName);
    }

    public boolean CodePageToCharset(int codePage, CkString outCharset) {
        return chilkatJNI.CkCharset_CodePageToCharset(swigCPtr, this, codePage, CkString.getCPtr(outCharset), outCharset);
    }

    public String codePageToCharset(int codePage) {
        return chilkatJNI.CkCharset_codePageToCharset(swigCPtr, this, codePage);
    }

    public boolean ConvertData(CkByteData inData, CkByteData outData) {
        return chilkatJNI.CkCharset_ConvertData(swigCPtr, this, CkByteData.getCPtr(inData), inData, CkByteData.getCPtr(outData), outData);
    }

    public boolean ConvertFile(String inPath, String destPath) {
        return chilkatJNI.CkCharset_ConvertFile(swigCPtr, this, inPath, destPath);
    }

    public boolean ConvertFileNoPreamble(String inPath, String destPath) {
        return chilkatJNI.CkCharset_ConvertFileNoPreamble(swigCPtr, this, inPath, destPath);
    }

    public boolean ConvertFromUnicode(String inData, CkByteData outBytes) {
        return chilkatJNI.CkCharset_ConvertFromUnicode(swigCPtr, this, inData, CkByteData.getCPtr(outBytes), outBytes);
    }

    public boolean ConvertFromUtf16(CkByteData uniData, CkByteData outMbData) {
        return chilkatJNI.CkCharset_ConvertFromUtf16(swigCPtr, this, CkByteData.getCPtr(uniData), uniData, CkByteData.getCPtr(outMbData), outMbData);
    }

    public boolean ConvertHtml(CkByteData inData, CkByteData outHtml) {
        return chilkatJNI.CkCharset_ConvertHtml(swigCPtr, this, CkByteData.getCPtr(inData), inData, CkByteData.getCPtr(outHtml), outHtml);
    }

    public boolean ConvertHtmlFile(String inPath, String destPath) {
        return chilkatJNI.CkCharset_ConvertHtmlFile(swigCPtr, this, inPath, destPath);
    }

    public boolean ConvertToUnicode(CkByteData inData, CkString outStr) {
        return chilkatJNI.CkCharset_ConvertToUnicode(swigCPtr, this, CkByteData.getCPtr(inData), inData, CkString.getCPtr(outStr), outStr);
    }

    public String convertToUnicode(CkByteData inData) {
        return chilkatJNI.CkCharset_convertToUnicode(swigCPtr, this, CkByteData.getCPtr(inData), inData);
    }

    public boolean ConvertToUtf16(CkByteData mbData, CkByteData outUniData) {
        return chilkatJNI.CkCharset_ConvertToUtf16(swigCPtr, this, CkByteData.getCPtr(mbData), mbData, CkByteData.getCPtr(outUniData), outUniData);
    }

    public boolean EntityEncodeDec(String str, CkString outStr) {
        return chilkatJNI.CkCharset_EntityEncodeDec(swigCPtr, this, str, CkString.getCPtr(outStr), outStr);
    }

    public String entityEncodeDec(String str) {
        return chilkatJNI.CkCharset_entityEncodeDec(swigCPtr, this, str);
    }

    public boolean EntityEncodeHex(String str, CkString outStr) {
        return chilkatJNI.CkCharset_EntityEncodeHex(swigCPtr, this, str, CkString.getCPtr(outStr), outStr);
    }

    public String entityEncodeHex(String str) {
        return chilkatJNI.CkCharset_entityEncodeHex(swigCPtr, this, str);
    }

    public boolean GetHtmlCharset(CkByteData inData, CkString outCharset) {
        return chilkatJNI.CkCharset_GetHtmlCharset(swigCPtr, this, CkByteData.getCPtr(inData), inData, CkString.getCPtr(outCharset), outCharset);
    }

    public String getHtmlCharset(CkByteData inData) {
        return chilkatJNI.CkCharset_getHtmlCharset(swigCPtr, this, CkByteData.getCPtr(inData), inData);
    }

    public String htmlCharset(CkByteData inData) {
        return chilkatJNI.CkCharset_htmlCharset(swigCPtr, this, CkByteData.getCPtr(inData), inData);
    }

    public boolean GetHtmlFileCharset(String htmlFilePath, CkString outCharset) {
        return chilkatJNI.CkCharset_GetHtmlFileCharset(swigCPtr, this, htmlFilePath, CkString.getCPtr(outCharset), outCharset);
    }

    public String getHtmlFileCharset(String htmlFilePath) {
        return chilkatJNI.CkCharset_getHtmlFileCharset(swigCPtr, this, htmlFilePath);
    }

    public String htmlFileCharset(String htmlFilePath) {
        return chilkatJNI.CkCharset_htmlFileCharset(swigCPtr, this, htmlFilePath);
    }

    public boolean HtmlDecodeToStr(String inStr, CkString outStr) {
        return chilkatJNI.CkCharset_HtmlDecodeToStr(swigCPtr, this, inStr, CkString.getCPtr(outStr), outStr);
    }

    public String htmlDecodeToStr(String inStr) {
        return chilkatJNI.CkCharset_htmlDecodeToStr(swigCPtr, this, inStr);
    }

    public boolean HtmlEntityDecode(CkByteData inHtml, CkByteData outData) {
        return chilkatJNI.CkCharset_HtmlEntityDecode(swigCPtr, this, CkByteData.getCPtr(inHtml), inHtml, CkByteData.getCPtr(outData), outData);
    }

    public boolean HtmlEntityDecodeFile(String inPath, String destPath) {
        return chilkatJNI.CkCharset_HtmlEntityDecodeFile(swigCPtr, this, inPath, destPath);
    }

    public boolean IsUnlocked() {
        return chilkatJNI.CkCharset_IsUnlocked(swigCPtr, this);
    }

    public boolean LowerCase(String inStr, CkString outStr) {
        return chilkatJNI.CkCharset_LowerCase(swigCPtr, this, inStr, CkString.getCPtr(outStr), outStr);
    }

    public String lowerCase(String inStr) {
        return chilkatJNI.CkCharset_lowerCase(swigCPtr, this, inStr);
    }

    public boolean ReadFile(String path, CkByteData outData) {
        return chilkatJNI.CkCharset_ReadFile(swigCPtr, this, path, CkByteData.getCPtr(outData), outData);
    }

    public boolean ReadFileToString(String path, String charset, CkString outStr) {
        return chilkatJNI.CkCharset_ReadFileToString(swigCPtr, this, path, charset, CkString.getCPtr(outStr), outStr);
    }

    public String readFileToString(String path, String charset) {
        return chilkatJNI.CkCharset_readFileToString(swigCPtr, this, path, charset);
    }

    public boolean SaveLastError(String path) {
        return chilkatJNI.CkCharset_SaveLastError(swigCPtr, this, path);
    }

    public void SetErrorBytes(CkByteData data) {
        chilkatJNI.CkCharset_SetErrorBytes(swigCPtr, this, CkByteData.getCPtr(data), data);
    }

    public void SetErrorString(String str, String charset) {
        chilkatJNI.CkCharset_SetErrorString(swigCPtr, this, str, charset);
    }

    public boolean UnlockComponent(String unlockCode) {
        return chilkatJNI.CkCharset_UnlockComponent(swigCPtr, this, unlockCode);
    }

    public boolean UpperCase(String inStr, CkString outStr) {
        return chilkatJNI.CkCharset_UpperCase(swigCPtr, this, inStr, CkString.getCPtr(outStr), outStr);
    }

    public String upperCase(String inStr) {
        return chilkatJNI.CkCharset_upperCase(swigCPtr, this, inStr);
    }

    public boolean UrlDecodeStr(String inStr, CkString outStr) {
        return chilkatJNI.CkCharset_UrlDecodeStr(swigCPtr, this, inStr, CkString.getCPtr(outStr), outStr);
    }

    public String urlDecodeStr(String inStr) {
        return chilkatJNI.CkCharset_urlDecodeStr(swigCPtr, this, inStr);
    }

    public boolean VerifyData(String charset, CkByteData inData) {
        return chilkatJNI.CkCharset_VerifyData(swigCPtr, this, charset, CkByteData.getCPtr(inData), inData);
    }

    public boolean VerifyFile(String charset, String path) {
        return chilkatJNI.CkCharset_VerifyFile(swigCPtr, this, charset, path);
    }

    public boolean WriteFile(String path, CkByteData byteData) {
        return chilkatJNI.CkCharset_WriteFile(swigCPtr, this, path, CkByteData.getCPtr(byteData), byteData);
    }

    public boolean WriteStringToFile(String textData, String path, String charset) {
        return chilkatJNI.CkCharset_WriteStringToFile(swigCPtr, this, textData, path, charset);
    }

}
