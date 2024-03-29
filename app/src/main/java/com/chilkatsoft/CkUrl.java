/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkUrl {
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    protected CkUrl(long cPtr, boolean cMemoryOwn) {
        swigCMemOwn = cMemoryOwn;
        swigCPtr = cPtr;
    }

    public CkUrl() {
        this(chilkatJNI.new_CkUrl(), true);
    }

    protected static long getCPtr(CkUrl obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                chilkatJNI.delete_CkUrl(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    public void get_Frag(CkString str) {
        chilkatJNI.CkUrl_get_Frag(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String frag() {
        return chilkatJNI.CkUrl_frag(swigCPtr, this);
    }

    public void get_Host(CkString str) {
        chilkatJNI.CkUrl_get_Host(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String host() {
        return chilkatJNI.CkUrl_host(swigCPtr, this);
    }

    public void get_HostType(CkString str) {
        chilkatJNI.CkUrl_get_HostType(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String hostType() {
        return chilkatJNI.CkUrl_hostType(swigCPtr, this);
    }

    public boolean get_LastMethodSuccess() {
        return chilkatJNI.CkUrl_get_LastMethodSuccess(swigCPtr, this);
    }

    public void put_LastMethodSuccess(boolean newVal) {
        chilkatJNI.CkUrl_put_LastMethodSuccess(swigCPtr, this, newVal);
    }

    public void get_Login(CkString str) {
        chilkatJNI.CkUrl_get_Login(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String login() {
        return chilkatJNI.CkUrl_login(swigCPtr, this);
    }

    public void get_Password(CkString str) {
        chilkatJNI.CkUrl_get_Password(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String password() {
        return chilkatJNI.CkUrl_password(swigCPtr, this);
    }

    public void get_Path(CkString str) {
        chilkatJNI.CkUrl_get_Path(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String path() {
        return chilkatJNI.CkUrl_path(swigCPtr, this);
    }

    public void get_PathWithQueryParams(CkString str) {
        chilkatJNI.CkUrl_get_PathWithQueryParams(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String pathWithQueryParams() {
        return chilkatJNI.CkUrl_pathWithQueryParams(swigCPtr, this);
    }

    public int get_Port() {
        return chilkatJNI.CkUrl_get_Port(swigCPtr, this);
    }

    public void get_Query(CkString str) {
        chilkatJNI.CkUrl_get_Query(swigCPtr, this, CkString.getCPtr(str), str);
    }

    public String query() {
        return chilkatJNI.CkUrl_query(swigCPtr, this);
    }

    public boolean get_Ssl() {
        return chilkatJNI.CkUrl_get_Ssl(swigCPtr, this);
    }

    public boolean ParseUrl(String url) {
        return chilkatJNI.CkUrl_ParseUrl(swigCPtr, this, url);
    }

}
