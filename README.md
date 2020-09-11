# RotateFragment
关于 fragmentation 如何只让某个Fragment支持横屏


众所周知，Android 如果用Activity做横屏处理是相当简单的，就一个配置解决，剩下的就是修改横屏相应的布局。

但是。Fragment要做的跟Activity那么完美就没那么简单了。
接下来就是我解决这个坑花了3天时间的总结（大致效果跟高德地图的导航界面旋转一样的效果）：

1.使用SensorManager重力感应，在初始化需要横屏的Fragment的时候，自己写根据感应角度旋转屏幕。

2.在onConfigurationChanged事件布置两个竖屏横屏的布局。

3.在打开别的Fragment或者返回上一个Fragment前，分别关闭重力感应，并且使它竖屏。

为什么要自己弄的那么麻烦，没办法，Android对于横竖屏的支持，只有Activity，无奈我优化这个App用的布局是单Activity和全部Fragment的方式。所以算是自己挖的深坑吧，但是总算完美解决了，代码冗余还是要解决下。

具体简书链接
https://www.jianshu.com/p/fc598263c136
