<<<<<<< HEAD
package com.isoftstone.crawl.template.test;

import com.isoftstone.crawl.template.utils.MD5Utils;
import com.isoftstone.crawl.template.utils.RedisUtils;

public class RemoveTemplateByGuid {

	public static void main(String[] args) {
		String url="http://www.zhongguoxintuo.com/xtxw/5971.html";
		String guid =MD5Utils.MD5(url);
		System.out.println(guid);
		System.out.println(RedisUtils.remove(guid,0));
	}

}
=======
package com.isoftstone.crawl.template.test;

import com.isoftstone.crawl.template.utils.MD5Utils;
import com.isoftstone.crawl.template.utils.RedisUtils;

public class RemoveTemplateByGuid {

	public static void main(String[] args) {
		String url="http://www.zhongguoxintuo.com/xtxw/5971.html";
		String guid =MD5Utils.MD5(url);
		System.out.println(guid);
		System.out.println(RedisUtils.remove(guid,0));
	}

}
>>>>>>> 3f0f16c7e97c1276b53d01ffd369a74c00ddaa37
