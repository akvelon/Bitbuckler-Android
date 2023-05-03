package com.akvelon.bitbuckler.model.entity.issueTracker.update

class IssueStatusChanges(
    val priority: ChangeModel? = null,
    val state: ChangeModel? = null,
    val kind: ChangeModel? = null,
)