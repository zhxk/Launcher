package com.changren.android.launcher.util;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Author: wangsy
 * Create: 2018-12-25 17:00
 * Description: Glide V4版本 用于使用注解处理器生成一个流式Generated API，用于RequestBuilder，RequestOptions等相关的所有选项。
 *  Glide的文档中有说明，其目的一是为了更好地扩展自定义选项，其二是为了方便打包常用选项组
 */
@GlideModule
public final class CustomAppGlideModule extends AppGlideModule {}
