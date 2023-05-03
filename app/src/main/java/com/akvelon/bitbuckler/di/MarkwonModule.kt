/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 22 March 2021
 */

package com.akvelon.bitbuckler.di

import android.content.Context
import android.text.util.Linkify
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.ui.glide.GlideApp
import com.akvelon.bitbuckler.ui.span.RoundedBackgroundSpan
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.simple.ext.SimpleExtPlugin
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val MENTION_PLUGIN = "MENTION_PLUGIN"
const val SELF_MENTION_PLUGIN = "SELF_MENTION_PLUGIN"

val markwonModule = module {

    single(named(MENTION_PLUGIN)) {
        mentionPlugin(get())
    }

    single(named(SELF_MENTION_PLUGIN)) {
        selfMentionPlugin(get())
    }

    single {
        markwon(get(), get(named(MENTION_PLUGIN)), get(named(SELF_MENTION_PLUGIN)))
    }

    single {
        markwonEditor(get())
    }
}

fun markwon(context: Context, mentionPlugin: SimpleExtPlugin, selfMentionPlugin: SimpleExtPlugin) =
    Markwon.builder(context)
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(GlideImagesPlugin.create(GlideApp.with(context)))
        .usePlugin(TablePlugin.create(context))
        .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
        .usePlugin(mentionPlugin)
        .usePlugin(selfMentionPlugin)
        .build()

fun mentionPlugin(context: Context) = SimpleExtPlugin.create { plugin ->
    plugin.addExtension(
        2,
        '@'
    ) { _, _ ->
        RoundedBackgroundSpan(
            backgroundColor = context.getColor(R.color.grayLight),
            textColor = context.getColor(R.color.black)
        )
    }
}

fun selfMentionPlugin(context: Context) = SimpleExtPlugin.create { plugin ->
    plugin.addExtension(
        3,
        '@'
    ) { _, _ ->
        RoundedBackgroundSpan(
            backgroundColor = context.getColor(R.color.blue),
            textColor = context.getColor(R.color.grayLight)
        )
    }
}

fun markwonEditor(markwon: Markwon) =
    MarkwonEditor.create(markwon)
