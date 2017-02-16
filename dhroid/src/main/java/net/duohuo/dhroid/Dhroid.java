package net.duohuo.dhroid;


import android.app.Application;
import net.duohuo.dhroid.db.DhDB;
import net.duohuo.dhroid.dialog.DialogImpl;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.Instance.InstanceScope;
import net.duohuo.dhroid.ioc.Ioc;
import net.duohuo.dhroid.ioc.IocContainer;
/**
 * 完成一些系统的初始化的工作
 * @author Administrator
 *
 */
public class Dhroid {
	public static void init(Application app){
		Ioc.initApplication(app);
		//对话框的配置
		Ioc.bind(DialogImpl.class).to(IDialog.class).scope(InstanceScope.SCOPE_PROTOTYPE);
	
		/*ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(app)
				.discCacheSize(50 * 1024 * 1024)//
				.discCacheFileCount(100)//缓存一百张图片
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);*/
		//数据库初始化
		DhDB db=IocContainer.getShare().get(DhDB.class);
		db.init("dhdbname", Const.DATABASE_VERSION);
	}
}
