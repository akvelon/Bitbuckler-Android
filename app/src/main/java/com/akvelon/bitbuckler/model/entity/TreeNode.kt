/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 20 January 2021
 */

package com.akvelon.bitbuckler.model.entity

import android.os.Parcel
import android.os.Parcelable
import com.akvelon.bitbuckler.extension.replaceElement
import java.util.function.Predicate

data class TreeNode<T : Parcelable>(
    val id: Int,
    var element: T,
    val level: Int,
    val children: MutableList<TreeNode<T>> = mutableListOf()
) : Parcelable {

    constructor(id: Int, element: T) : this(id, element, 0)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(TreeNode<T>::element.javaClass.classLoader)!!,
        parcel.readInt(),
        mutableListOf<TreeNode<T>>().also {
            parcel.readList(it, TreeNode<T>::children.javaClass.classLoader)
        }
    )

    private var toRemove = false

    val count
        get() = getElementsCount()

    val hasNoChild
        get() = children.isEmpty()

    val isEmptyTree
        get() = toRemove

    fun addChild(child: TreeNode<T>) {
        children.add(child)
    }

    fun findById(id: Int): TreeNode<T>? {
        if (this.id == id) {
            return this
        }

        var result: TreeNode<T>? = null

        if (children.isNotEmpty()) {
            for (child in children) {
                result = child.findById(id)
                if (result != null) {
                    break
                }
            }
        }

        return result
    }

    fun flatten(): List<TreeNode<T>> {
        if (children.isEmpty()) {
            return listOf(this)
        }

        val mutableList = mutableListOf(this)
        children.forEach {
            mutableList.addAll(it.flatten())
        }

        return mutableList.toList()
    }

    fun removeIf(filter: Predicate<TreeNode<T>>) {
        children.removeIf { child ->
            child.removeIf(filter)
            child.toRemove
        }

        toRemove = filter.test(this)
    }

    fun replaceChild(childId: Int, child: T) {
        children.find { node -> node.id == childId }?.let {
            val newNode = it.copy().apply { element = child }
            children.replaceElement(it, newNode)
        }
    }

    private fun getElementsCount(): Int {
        var result = 1 + children.count()
        children.forEach { child -> result += child.getElementsCount() - 1 }
        return result
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeParcelable(element, 0)
        writeInt(level)
        writeList(children)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<TreeNode<Parcelable>> {
            override fun createFromParcel(source: Parcel) = TreeNode<Parcelable>(source)

            override fun newArray(size: Int) = arrayOfNulls<TreeNode<Parcelable>>(size)
        }
    }
}
