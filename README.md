# ExpandableLayout
# 可折叠列表

https://github.com/cachapa/ExpandableLayout

<com.runtai.expandablelayoutlibrary.ExpandableLayout
    android:id="@+id/expandable_layout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:el_duration="1000"
    app:el_expanded="true"
    app:el_parallax="0.5">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp"
        android:text="Fixed height" />
</com.runtai.expandablelayoutlibrary.ExpandableLayout>

xml中定义
app:el_duration //设置列表展开、关闭的时间
app:el_expanded //设置默认是否展开
app:el_parallax //设置为0到1之间的值 //can be set to a value between 0 and 1 to control how the child view is translated during the expansion
java代码中设置
expand() //设置展开
collapse() //设置关闭
toggle() //根据现有状态设置取反状态设置