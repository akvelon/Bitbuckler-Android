/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Aleksandra Domashina (aleksandra.domashina@akvelon.com)
 * on 24 March 2021
 */

package com.akvelon.bitbuckler.model.entity.repository.issue

enum class IssueFilter(val position: Int) {
    ALL(0),
    OPEN(1),
    MINE(2)
}
