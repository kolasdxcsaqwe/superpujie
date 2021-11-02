package com.nwf.app.ui.activity.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import com.common.util.CfLog;
import com.common.util.Check;
import com.common.util.DeviceUtils;
import com.common.util.GameLog;
import com.dawoo.coretool.util.activity.ActivityStackManager;
import com.goldarmor.live800lib.live800sdk.manager.LIVManager;
import com.goldarmor.live800lib.live800sdk.request.LIVUserInfo;
import com.google.gson.Gson;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.nwf.app.ConstantValue;
import com.nwf.app.R;
import com.nwf.app.mvp.model.NEnterGameResult;
import com.nwf.app.mvp.model.UserInfoBean;
import com.nwf.app.mvp.model.UserInformJS;
import com.nwf.app.mvp.presenter.NEnterGamePresenter;
import com.nwf.app.mvp.view.EnterGameView;
import com.nwf.app.ui.activity.ElectronicGame.ElectronicGameActivity;
import com.nwf.app.ui.activity.Home.UserActivityListActivity;
import com.nwf.app.ui.activity.MainActivity;
import com.nwf.app.ui.activity.OnlineServiceActivity;
import com.nwf.app.ui.activity.RegisterAndLogin.LoginActivity;
//import com.nwf.app.ui.activity.me.CreateOrEditVirtualBankActivity;
//import com.nwf.app.ui.activity.me.ModifyPhoneActivity;
//import com.nwf.app.ui.activity.me.MyBankManagementActivity;
//import com.nwf.app.ui.activity.me.RedEnvelopActivity;
//import com.nwf.app.ui.activity.me.UserOperationRecordActivity;
import com.nwf.app.ui.activity.RegisterAndLogin.RegisterActivity;
import com.nwf.app.ui.activity.me.CreateOrEditVirtualBankActivity;
import com.nwf.app.ui.activity.me.ModifyPhoneActivity;
import com.nwf.app.ui.activity.me.MyBankManagementActivity;
import com.nwf.app.ui.activity.me.RedEnvelopActivity;
import com.nwf.app.ui.activity.me.UserOperationRecordActivity;
import com.nwf.app.ui.base.BaseActivity;
import com.nwf.app.ui.views.NTitleBar;
import com.nwf.app.ui.views.patch.AndroidBug5497Workaround;
import com.nwf.app.ui.views.patch.PTIWebSetting;
import com.nwf.app.utils.Enum.GameListNameEnum;
import com.nwf.app.utils.TitleBarHelper;
import com.nwf.app.utils.data.DataCenter;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.PermissionRequest;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

public class IntroduceActivity extends BaseActivity implements EnterGameView
{


    private static String EVENT_RELOAD = "event_reload";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    /*@BindView(R.id.iv_homepage_introduce_cancel)
    ImageView ivHomepageIntroduceCancel;
    @BindView(R.id.tv_homepage_introduce_title)
    TextView tvHomepageIntroduceTitle;
    @BindView(R.id.wv_homepage_introduce_content)*/
    @BindView(R.id.flayout_record)
    FrameLayout frameLayoutRecord;
    @BindView(R.id.com_homepage_introduce_title)
    NTitleBar introduceTitle;
    @BindView(R.id.wv_homepage_introduce_content)
    WebView wvHomepageIntroduceContent;
    private boolean bindPhone = false;
    private boolean clickBackExit=false;
    OnFinishListener onFinishListener;
    private String mParam1;
    private String mParam2;

    NEnterGamePresenter nEnterGamePresenter;

    public static void startActivity(Context context,String title, String url,boolean clickBackExit) {
        GameLog.log("param1: " + title + ", param2: " + url);
        Intent intent=new Intent(context, IntroduceActivity.class);
        intent.putExtra(ARG_PARAM1, title);
        intent.putExtra(ARG_PARAM2, url);
        intent.putExtra(ARG_PARAM3,clickBackExit);

        context.startActivity(intent);
    }

    public static void startActivity(Context context,String title, String url) {
        GameLog.log("param1: " + title + ", param2: " + url);
        Intent intent=new Intent(context, IntroduceActivity.class);
        intent.putExtra(ARG_PARAM1, title);
        intent.putExtra(ARG_PARAM2, url);

        context.startActivity(intent);
    }

    @Override
    protected void createLayoutView() {
        setContentView(R.layout.activity_introduce);
    }

    @Override
    protected void initViews() {

        if (getIntent() != null ) {
            mParam1 = getIntent().getStringExtra(ARG_PARAM1);
            mParam2 = getIntent().getStringExtra(ARG_PARAM2);
            clickBackExit=getIntent().getBooleanExtra(ARG_PARAM3,false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wvHomepageIntroduceContent.setWebContentsDebuggingEnabled(true);
        }
        AndroidBug5497Workaround.assistActivity(this);

        nEnterGamePresenter=new NEnterGamePresenter(this,this);
        GameLog.log("title:" + mParam1 + "\nUrl:" + mParam2);
        introduceTitle.setTitle(mParam1);
        introduceTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlineServiceActivity.startActivity(IntroduceActivity.this);
            }
        });
        introduceTitle.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickBackExit)
                {
                    ActivityStackManager.getInstance().finishToActivity(MainActivity.class,true);
                }
                else
                {
                    finish();
                }

            }
        });
        introduceTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParam1.equals("存款小助手")) {
                    String username = DataCenter.getInstance().getUserInfoBean().getUsername();
                    if(username==null)
                    {
                        username="游客";
                    }
                    LIVUserInfo info = new LIVUserInfo();
                    //设置用户唯一id
                    info.setUserId(username);
                    //设置用户姓名
                    info.setName(username);
                    LIVManager.getInstance().startService(IntroduceActivity.this, info);
                    finish();
                } else {
                    OnlineServiceActivity.startActivity(IntroduceActivity.this);
                }
            }
        });

        PTIWebSetting.init(wvHomepageIntroduceContent);
        //webviewsetting(wvHomepageIntroduceContent);
        // Android Webview 默认状态下是不支持LocalStorage的，我们要进行以下设置：
        WebSettings websettings = wvHomepageIntroduceContent.getSettings();
        websettings.setDomStorageEnabled(true);  // 开启 DOM storage 功能
        websettings.setAppCacheMaxSize(1024 * 1024 * 1);
        String appCachePath =getApplicationContext().getCacheDir().getAbsolutePath();
        websettings.setAppCachePath(appCachePath);
        websettings.setAllowFileAccess(true);    // 可以读取文件缓存
        websettings.setAppCacheEnabled(true);    //开启H5(APPCache)缓存功能


        wvHomepageIntroduceContent.addJavascriptInterface(new RecordsInterface(), "AndroidWebView");
        wvHomepageIntroduceContent.setWebChromeClient(new CommonWebChromeClient());
        wvHomepageIntroduceContent.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                //
                GameLog.log("提前加载JS文件");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("TT", url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });


        wvHomepageIntroduceContent.loadUrl("" + mParam2);
//        wvHomepageIntroduceContent.loadUrl("http://10.91.11.23:8080/celebration13.htm?APP");
        //wvHomepageIntroduceContent.postUrl();
    }




    @Override
    protected void initData() {

    }

    @Override
    public void setEnterGameResult(NEnterGameResult nEnterGameResult) {
        if (Check.isNull(nEnterGameResult)) {
            return;
        }
        if (GameListNameEnum.E03039.getPidCode().equals(nEnterGameResult.getSupplyId())) {
            TitleBarHelper.transitToPTGame(this, nEnterGameResult.getGameUrl(), nEnterGameResult.getPtLoginName(), nEnterGameResult.getPtGameKey(),
                    nEnterGameResult.getPtKey(), nEnterGameResult.getPtMd5(), nEnterGameResult.getSupplyId());
        } else {
            TitleBarHelper.transitToGame(this, nEnterGameResult.getSupplyId()
                    ,nEnterGameResult.getGameId(),
                    nEnterGameResult.getGameUrl(),
                    nEnterGameResult.getType(),nEnterGameResult.getUuId(),nEnterGameResult.getTitle());
        }


    }


    public class RecordsInterface {


        public RecordsInterface() {
        }

        @JavascriptInterface
        public void goGamesList() {
            GameLog.log("javascriptToJava( )");
            goGamesList("", "");
        }

        @JavascriptInterface
        public void goEletronicGame(String json) {
            GameLog.log("goEletronicGame( ) "+json);
            try {
                JSONObject jsonObject=new JSONObject(json);
                String gameName=jsonObject.optString("gameName","");
                String gameId=jsonObject.optString("gameId","");
                String supplierId=jsonObject.optString("supplierId","");
                String line=jsonObject.optString("line","");
                if(TextUtils.isEmpty(supplierId))
                {
//                    wvHomepageIntroduceContent.loadUrl("javascript:console.log('gameId 为空,来自goEletronicGame')");
                    return;
                }

                if(TextUtils.isEmpty(line))
                {
                    EletronicGameChooseLine(supplierId,gameId,gameName);
                }
                else
                {
                    switch (line.trim().toLowerCase())
                    {
                        case "usdt":
                            EletronicGameUSDTLine(supplierId,gameId,gameName);
                            break;
                        case "cny":
                            EletronicGameCNYLine(supplierId,gameId,gameName);
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void EletronicGameChooseLine(String gameCode,String gameId,String gameName)
        {
            GameLog.log("EletronicGameChooseLine(    )");
            //捕鱼 E04026 typeid 5 //首页游戏 除了捕鱼王传6 其他游戏不用传gameId
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    nEnterGamePresenter.onEnterGameWithDialog(getSupportFragmentManager(),gameId,
                            gameCode,gameName);
                }
            });

        }

        private void EletronicGameCNYLine(String gameCode,String gameId,String gameName)
        {
            GameLog.log("EletronicGameCNYLine(    )");
            //捕鱼 E04026 typeid 5 //首页游戏 除了捕鱼王传6 其他游戏不用传gameId
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    nEnterGamePresenter.onEnterGame(gameId, gameCode,"",gameName,"",true);
                }
            });

        }

        private void EletronicGameUSDTLine(String gameCode,String gameId,String gameName)
        {
            GameLog.log("EletronicGameUSDTLine(    )");
            //捕鱼 E04026 typeid 5 //首页游戏 除了捕鱼王传6 其他游戏不用传gameId
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    nEnterGamePresenter.onEnterGame(gameId,
                            gameCode,"USDT",gameName,"",true);
                }
            });

        }

        @JavascriptInterface
        public void goGamesList(String actionName, String param) {
            GameLog.log("goGamesList( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                    ElectronicGameActivity.startActivity(IntroduceActivity.this);
                }
            });
        }

        @JavascriptInterface
        public void goFishing() {
            GameLog.log("goFishing( )");
            goFishing("", "");
        }

        @JavascriptInterface
        public void goFishing(String actionName, String param) {
            GameLog.log("goFishing( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    //捕鱼 E04026 typeid 5 //首页游戏 除了捕鱼王传6 其他游戏不用传gameId
                    nEnterGamePresenter.onEnterGameWithDialog(getSupportFragmentManager(),"6",
                            GameListNameEnum.E03026.getPidCode(),"AG捕鱼");
                }
            });
        }

        @JavascriptInterface
        public void goOnlineSports() {
            GameLog.log("goOnlineSports( )");
            goOnlineSports("", "");
        }

        @JavascriptInterface
        public void goOnlineSports(String actionName, String param) {
            GameLog.log("goOnlineSports( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    //沙巴 E04031 typeid 1
                    nEnterGamePresenter.onEnterGameWithDialog(getSupportFragmentManager(),"",
                            GameListNameEnum.E03031.getPidCode(),"沙巴体育");

                }
            });
        }

        /**
         *跳转首页
         */
        @JavascriptInterface
        public void goIndex() {
            GameLog.log("goIndex( )");
            goIndex("","");
        }

        /**
         *跳转首页
         */
        @JavascriptInterface
        public void goIndex(String s1, String s2) {
            GameLog.log("goIndex(   " + s1 + "," + s2 + " )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    ActivityStackManager.getInstance().finishToActivity(MainActivity.class,true);
                    RxBus.get().post(ConstantValue.MAINACTIVITY_SWITCHOVER, MainActivity.TAB_INDEX_HOME);
                }
            });


        }

        /**
         * 跳转到优惠记录列表 2018-08-10
         */
        @JavascriptInterface
        public void goSpecialOffer() {//String actionName,String param
            GameLog.log("goIndex( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    finish(); // hide();
                    UserOperationRecordActivity.startActivity(IntroduceActivity.this);
                }
            });

        }

        @JavascriptInterface
        public void goAGIN() {
            GameLog.log("goAGIN( )");
            goAGIN("", "");
        }

        @JavascriptInterface
        public void goAGIN(String actionName, String param) {
            GameLog.log("goAGIN(     )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    //AG国际 E04026 typeid 3
                    nEnterGamePresenter.onEnterGameWithDialog(getSupportFragmentManager(),"",
                            GameListNameEnum.E03026.getPidCode(),"AG国际");
                }
            });
        }

        @JavascriptInterface
        public void goAG() {
            GameLog.log("goAG( )");
            goAG("", "");
        }

        @JavascriptInterface
        public void goAG(String actionName, String param) {
            GameLog.log("goAG(    )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    //AG旗舰 E04003 typeid 3
                    nEnterGamePresenter.onEnterGameWithDialog(getSupportFragmentManager(),"",
                            GameListNameEnum.E03003.getPidCode(),"AG旗舰");
                }
            });
        }

        /**
         * 在线客服 2018-8-8
         */
        @JavascriptInterface
        public void goOnlineService() {
            GameLog.log("goOnlineService( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                    OnlineServiceActivity.startActivity(IntroduceActivity.this);
                }
            });
        }

        @JavascriptInterface
        public void goRegister() {
            GameLog.log("goRegister( )");
            goRegister("", "");
        }

        @JavascriptInterface
        public void goRegister(String actionName, String param) {
            GameLog.log("goRegister(    )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                    RegisterActivity.startActivity(IntroduceActivity.this,"","");
                }
            });
        }

        @JavascriptInterface
        public void goLogin() {
            GameLog.log("javascriptToJava( )");
            goLogin("", "");
        }

        @JavascriptInterface
        public void goLogin(String actionName, String param) {
            GameLog.log("goLogin(   " + actionName + ", " + param + "   )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    GameLog.log("IntroduceFragment javascriptTOjava pop()");
                    finish();
                    LoginActivity.startActivity(IntroduceActivity.this,"","");
                }
            });
        }

        /**
         * 跳转个人中心
         */
        @JavascriptInterface
        public void goPersonalInfo() {
            GameLog.log("goPersonalInfo( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    GameLog.log("IntroduceFragment javascriptTOjava pop()  goPersonalInfo");
                    ActivityStackManager.getInstance().finishToActivity(MainActivity.class,true);
                    RxBus.get().post(ConstantValue.MAINACTIVITY_SWITCHOVER, MainActivity.TAB_INDEX_MINE);
                }
            });
        }


        @JavascriptInterface
        public void getLoginInfo() {
            GameLog.log("javascriptToJava getLoginInfo( )");
            //必须开启线程进行JS调用
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    UserInfoBean userInfoBean= DataCenter.getInstance().getUserInfoCenter().getUserInfoBean();

                    UserInformJS userInformJS = new UserInformJS();
                    userInformJS.isNewAPP="true";
                    userInformJS.isLogin = DataCenter.getInstance().getUserInfoCenter().isRealLogin() ? "true" : "false";
                    userInformJS.account = userInfoBean.getUsername(); // 试玩账号不传值
                    userInformJS.star = userInfoBean.getLevelNum();
                    userInformJS.token = userInfoBean.getToken();
                    userInformJS.drpToken=userInfoBean.getDrpToken();
                    userInformJS.hasBankCard = "" + userInfoBean.isHasBankCard(); // 2018-08-31
                    if(DeviceUtils.getLocalInetAddress()!=null)
                    {
                        userInformJS.ip = DeviceUtils.getLocalInetAddress().getHostAddress();
                    }
                    else
                    {
                        userInformJS.ip = "";
                    }
                    userInformJS.mac = DeviceUtils.getMacAddress();
                    userInformJS.usdtAccount = DataCenter.getInstance().isUsdt() ? "true" : "false";
                    String userInformJson = new Gson().toJson(userInformJS);
                    //wvAd.loadData("","text/html","UTF-8");
                    if (!Check.isNull(wvHomepageIntroduceContent)) {
                        wvHomepageIntroduceContent.loadUrl("javascript:getLoginInfo('" + userInformJson + "')");
                    }
                }
            });

        }

        @JavascriptInterface
        public void goDeposit() {
            GameLog.log("goDeposit( )");
            goDeposit("", "");
        }

        /**
         * 跳转到存款界面 2018-08-10
         */
        @JavascriptInterface
        public void goDeposit(String actionName, String param) {
            GameLog.log("goDeposit( " + actionName + "," + param + " )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    ActivityStackManager.getInstance().finishToActivity(MainActivity.class,true);
                    RxBus.get().post(ConstantValue.MAINACTIVITY_SWITCHOVER, MainActivity.TAB_INDEX_DEPOSIT);
                }
            });
        }

        @JavascriptInterface
        public void goWithdraw() {
            GameLog.log("goWithdraw( )");
            goWithdraw("", "");
        }

        /**
         * 跳转到存款界面 2018-08-10
         */
        @JavascriptInterface
        public void goWithdraw(String actionName, String param) {
            GameLog.log("goWithdraw( " + actionName + "," + param + " )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    ActivityStackManager.getInstance().finishToActivity(MainActivity.class,true);
                    RxBus.get().post(ConstantValue.MAINACTIVITY_SWITCHOVER, MainActivity.TAB_INDEX_WITHDRAWAL);
                }
            });

        }

        /**
         * 跳转到活动详情 2018-08-10
         *
         * @param actionName URL
         * @param param      title
         */
        @JavascriptInterface
        public void actDetail(String actionName, String param) {
            GameLog.log("actDetail(" + actionName + ", " + param + ")");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    IntroduceActivity.startActivity(IntroduceActivity.this,param,actionName);
                    finish();
                }
            });

        }

        /**
         * 跳转个人中心 推荐奖金
         */
        @JavascriptInterface
        public void goPersonalInfoAgency() {
            GameLog.log("goPersonalInfoAgency( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    ActivityStackManager.getInstance().finishToActivity(MainActivity.class,true);
                    RxBus.get().post(ConstantValue.MAINACTIVITY_SWITCHOVER, MainActivity.TAB_INDEX_DEPOSIT);
                }
            });
        }

        /**
         * 关闭窗体, 关闭当前界面 2018-08-10
         */
        @JavascriptInterface
        public void onCloseFromJS() {
            GameLog.log("onCloseFromJS( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

        @JavascriptInterface
        public void onCloseFromJS2() {
            GameLog.log("onCloseFromJS2( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

        /**
         * 关闭窗体, 关闭当前界面, 并通知上个页面刷新 2018-08-27
         */
        @JavascriptInterface
        public void closeAndReloadLastPage() {
            CfLog.i("closeAndReloadLastPage( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    wvHomepageIntroduceContent.reload();
                }
            });

        }

        @JavascriptInterface
        public void openNewWebView() {
            GameLog.log("openNewWebView( )");
            openNewWebView("", "");
        }


        /**
         * 跳转电子游戏
         *
         *  String type, String gmId,  String egameType,  String egameName，String title
         *  type类型 cny usdt 无论大小写 ，传空就是cny title是页面的标题
         *
         */
        @JavascriptInterface
        public void enterEGame() {
            GameLog.log("enterEGame");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    if(!DataCenter.getInstance().isUsdt())
                    {
                        nEnterGamePresenter.onEnterGame("",GameListNameEnum.E03003.getPidCode(),"3","AG旗舰",null,true);
                    }
                    else
                    {
                        nEnterGamePresenter.onEnterGameWithDialog(getSupportFragmentManager(),"",
                                GameListNameEnum.E03003.getPidCode(),"AG旗舰");
                    }
                }
            });

        }


        /**
         * 跳转新的活动页
         *
         * @param actionName URL
         * @param param      title
         */
        @JavascriptInterface
        public void openNewWebView(String actionName, String param) {
            GameLog.log("openNewWebView (" + actionName + ", " + param + ")");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    IntroduceActivity.startActivity(IntroduceActivity.this,param,actionName,clickBackExit);
                }
            });

        }

        @JavascriptInterface
        public void toFillInfomation() {
            GameLog.log("toFillInfomation( )");
            toFillInfomation("", "");
        }

        /**
         * 完善资料 2018-08-10
         *
         * @param actionName URL
         * @param param      title
         */
        @JavascriptInterface
        public void toFillInfomation(final String actionName, final String param) {
            GameLog.log("toFillInfomation (" + actionName + ", " + param + ", bindPhone " + bindPhone + ")");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                    if (bindPhone) {
                        MyBankManagementActivity.startActivity(IntroduceActivity.this);
                    } else {
                        ModifyPhoneActivity.startActivity(IntroduceActivity.this,true,
                                mParam1, mParam2);
                    }
                }
            });

        }


        @JavascriptInterface
        public void goWithdrawalInfo()
        {
            GameLog.log("goWithdrawalInfo( )");
            goWithdrawalInfo("", "");
        }

        /**
         * 跳转USDT添加虚拟卡
         *
         * @param actionName URL
         * @param param      title
         */
        @JavascriptInterface
        public void goWithdrawalInfo(final String actionName, final String param) {
            GameLog.log("goWithdrawalInfo (" + actionName + ", " + param + ", bindPhone " + bindPhone + ")");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                    //EventBus.getDefault().post(new ADEvent(2,ActionOnFinish.GO_ADD_BANK));
                    CreateOrEditVirtualBankActivity.startActivity(IntroduceActivity.this,"add", null,"");
                }
            });

        }

        @JavascriptInterface
        public void goSysBrowser(String url) {
            GameLog.log("goSysBrowser: " + url);
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });

        }

        /**
         * 跳转到优惠活动列表 2018-08-14
         */
        @JavascriptInterface
        public void goPromotionList() {
            GameLog.log("goPromotionList( )");

            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {
                    UserActivityListActivity.startActivity(IntroduceActivity.this);
                }
            });

        }

        /**
         * 跳转到我的优惠
         */
        @JavascriptInterface
        public void goDiscountsList() {
            GameLog.log("goDiscountsList( )");
            wvHomepageIntroduceContent.post(new Runnable() {
                @Override
                public void run() {

                    if (!DataCenter.getInstance().getUserInfoCenter().isRealLogin()) {
                        showMessage("请先登录");
                    } else {
                        RedEnvelopActivity.startActivity(IntroduceActivity.this);
                    }
                }
            });
        }



    }

    @Subscribe
    public void onEventMain(String event) {
        CfLog.i("event: " + event);
        if (EVENT_RELOAD.equals(event)) {
            wvHomepageIntroduceContent.reload();
        }
    }


    @Override
    public void onDestroy() {
        AndroidBug5497Workaround.adjustment = false;
        super.onDestroy();
        try {
            if(wvHomepageIntroduceContent!=null)
            {
                ViewParent parent = wvHomepageIntroduceContent.getParent();
                if (!com.dawoo.coretool.util.Check.isNull(parent)) {
                    ((ViewGroup) parent).removeAllViews();
                }
                wvHomepageIntroduceContent.stopLoading();
                wvHomepageIntroduceContent.removeAllViewsInLayout();
                wvHomepageIntroduceContent.removeAllViews();
                wvHomepageIntroduceContent.destroy();
                wvHomepageIntroduceContent=null;
            }
        }
        catch (Exception e)
        {
            Log.e(getClass().getName(),Log.getStackTraceString(e));
        }

    }

    /**
     * 调用相册
     */
    /**
     * Android 5.0以下版本的文件选择回调
     */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * Android 5.0及以上版本的文件选择回调
     */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;
    protected String mUploadableFileTypes = "image/*";

    protected static final int REQUEST_CODE_FILE_PICKER = 51426;

    private class CommonWebChromeClient extends WebChromeClient {

        @Override
        public void onPermissionRequest(PermissionRequest permissionRequest) {
//                super.onPermissionRequest(permissionRequest);
            permissionRequest.grant(permissionRequest.getResources());
        }

        @Override
        public void onPermissionRequestCanceled(PermissionRequest permissionRequest) {
//                super.onPermissionRequestCanceled(permissionRequest);

        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.e("Intro_Console","Line:"+consoleMessage.lineNumber()+" Log:"+consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            // setProgressBar(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //  setWebViewTitleName(title);
        }

        //  Android 2.2 (API level 8)到Android 2.3 (API level 10)版本选择文件时会触发该隐藏方法
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, null);
        }

        // Android 3.0 (API level 11)到 Android 4.0 (API level 15))版本选择文件时会触发，该方法为隐藏方法
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, null);
        }

        // Android 4.1 (API level 16) -- Android 4.3 (API level 18)版本选择文件时会触发，该方法为隐藏方法
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileInput(uploadMsg, null, false);
        }

        // Android 5.0 (API level 21)以上版本会触发该方法，该方法为公开方法
        @SuppressWarnings("all")
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (Build.VERSION.SDK_INT >= 21) {
                final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;//是否支持多选
                openFileInput(null, filePathCallback, allowMultiple);
                return true;
            } else {
                return false;
            }
        }

        @SuppressLint("NewApi")
        protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
            //Android 5.0以下版本
            if (mFileUploadCallbackFirst != null) {
                mFileUploadCallbackFirst.onReceiveValue(null);
            }
            mFileUploadCallbackFirst = fileUploadCallbackFirst;

            //Android 5.0及以上版本
            if (mFileUploadCallbackSecond != null) {
                mFileUploadCallbackSecond.onReceiveValue(null);
            }
            mFileUploadCallbackSecond = fileUploadCallbackSecond;

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);

            if (allowMultiple) {
                if (Build.VERSION.SDK_INT >= 18) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
            }

            i.setType(mUploadableFileTypes);
            startActivityForResult(Intent.createChooser(i, "选择文件"), REQUEST_CODE_FILE_PICKER);
        }

        IX5WebChromeClient.CustomViewCallback mCallBack;

        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
            fullScreen();
            wvHomepageIntroduceContent.setVisibility(View.GONE);
            frameLayoutRecord.setVisibility(View.VISIBLE);
            frameLayoutRecord.addView(view);
            mCallBack = customViewCallback;
            super.onShowCustomView(view, customViewCallback);
        }

        @Override
        public void onShowCustomView(View view, int i, IX5WebChromeClient.CustomViewCallback customViewCallback) {
            fullScreen();
            wvHomepageIntroduceContent.setVisibility(View.GONE);
            frameLayoutRecord.setVisibility(View.VISIBLE);
            frameLayoutRecord.addView(view);
            mCallBack = customViewCallback;
            super.onShowCustomView(view, i, customViewCallback);
        }

        @Override
        public void onHideCustomView() {
            fullScreen();
            if (mCallBack != null) {
                mCallBack.onCustomViewHidden();
            }
            wvHomepageIntroduceContent.setVisibility(View.VISIBLE);
            frameLayoutRecord.removeAllViews();
            frameLayoutRecord.setVisibility(View.GONE);
            super.onHideCustomView();
        }

//        @Override
//        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
//            return super.onJsAlert(webView, s, s1, jsResult);
//        }
//
//        @Override
//        public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
//            return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
//        }
//
//        @Override
//        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
//            return super.onJsConfirm(webView, s, s1, jsResult);
//        }
    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_FILE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    //Android 5.0以下版本
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {//Android 5.0及以上版本
                        Uri[] dataUris = null;

                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                            } else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                //这里mFileUploadCallbackFirst跟mFileUploadCallbackSecond在不同系统版本下分别持有了
                //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
                //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        if(wvHomepageIntroduceContent!=null)
        {
            wvHomepageIntroduceContent.removeJavascriptInterface("AndroidWebView");
        }
        if(onFinishListener!=null)
        {
            onFinishListener.onFinish();
        }
    }
    public interface OnFinishListener
    {
        public void onFinish();
    }
}
