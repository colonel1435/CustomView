package com.zero.customview.net;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/8/16 0016 15:11
 */

public interface RetrofitService {
    @GET("/Maintplatform/identify/getidentifyidentifyService.action")
    Observable<String> getInspectContent();

}
