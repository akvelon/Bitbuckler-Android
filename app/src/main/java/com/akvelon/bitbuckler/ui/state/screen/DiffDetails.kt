/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 02 February 2021
 */

package com.akvelon.bitbuckler.ui.state.screen

import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileDiff
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileStat
import com.akvelon.bitbuckler.ui.state.ScreenValue

class DiffDetails(

    val changes: List<FileDiff>,

    val files: List<FileStat>
) : ScreenValue {

    override val isEmpty = changes.isEmpty() && files.isEmpty()
}
