### 观察者模式--Observer

很久以前我研究过这个模式，当时只觉得神奇但是没掌握要领，所以理解不透彻。

所以再次研究，一定要搞清楚其中奥妙。

例子来自《敏捷软件开发原则、模式与实践》---Robert C.Martin。

#### step1：初步代码

interface TimeSource: 可理解为输入的时间资源，包含时分秒数据。

interface TimeSink: 可理解为时间输出装置，根据TimeSource传入的时间资源显示一个想要的时间格式。

class ClockDriver: 理解为制造工厂，原料是TimeSource，产品是TimeSink。

TimeSource接口持有ClockDriver,ClockDriver也持有一个TimeSource，互相持有。

ClockDriver还持有一个TimeSink。

这一步可以看到，原料(TimeSource的属性值)一变，产品(TimeSink的输出值)就立马跟着变了。

我想用大白话解释一下这个过程发生了什么：

1. 采集原料：
```java
//source就是原料
MockTimeSource source=new MockTimeSource();
```
2. 原料送到工厂：
```java
//driver就是工厂，原料source作为构造函数参数送到了工厂
ClockDriver driver = new ClockDriver(source,sink);
```

3. 原料变质了(source属性被赋值)
```java
source.setTime(2,3,4);
//.....
source.setTime(7,8,9);

``` 

4. 产品也变了(sink改变)
```java
assertEquals(7,sink.getHours());
```


但这一步设计却是让TimeSource持有一个ClockDriver，再让ClockDriver中的TimeSource实例MockTimeSource
中的类方法setTime(...)去给TimeSink的时分秒赋值。

很直观的感受到，这设计不合理：

1. TimeSource为什么要持有一个ClockDriver？产品的原料不应该知道它的工厂。

2. MockTimeSource的setTime方法不是接口TimeSource约定方法。

3. TimeSource应该需要知道它的资源，所以应该设计一些getter方法。

作者也觉得第一个不合理应修正，请看step2。

#### step2

这一步抽象了一个ClockObserver接口，让ClockDriver实现它。TimeSource与ClockDriver解耦，但多了一个更高层的抽象：ClockObserver。

##### 观察者来了

观察者来了，它取代了ClockDriver的地位。我们可以认为观察者是一个观察原料变化，并将这个变化应用到产品的这么一个对象。

因为观察者抽象成为了接口，这样一来，啥东东都可以成为观察者。


现在，我们应该能初步理解这种设计模式的初衷了吧。它就是抽象一个观察者对象，观察一个对象的变化，将这个变化反映出来。

代码其它没做改变，测试也passed。

不过我还有个问题，source还是要和observer耦合，这不合理。作者认为让source本身变成观察者，然后把自己设置为source的observer
设置为自己，大概可以节省代码行数：
```java
// 注意，作者并没有这样写这个类，这是我根据作者的意思自己编的
public class MockTimeSource implements TimeSource, ClockObserver {
    private ClockObserver observer;


    void setTime(int hours, int minutes, int seconds) {
        observer.update(hours, minutes, seconds);
    }

    @Override
    public void update(int hours, int minutes, int seconds) {
        /**
         *  codes here 
         */
    }


    //这个方法形参好像多余了
    @Override
    public void setObserver(ClockObserver driver) {
        this.observer = this;
    }
}

```

作者让source持有观察者的本意是，source的改变可立即通知观察者，让观察者去更新sink。

看上去并不优雅。我认为，让观察者持有source和sink才是正确的方式，而不是让source持有observer。看看作者后面会不会这样搞。

#### step3

作者第三步是让MockTimeSink实现ClockObserver，所以不需要ClockDriver，省掉了ClockDriver这个类。

他还把source的setObserver变成了registerObserver，让source拥有多种类型的observer。

另外一个重大修改是，TimeSource从接口变成了类。这样做的好处是，
TimeSource的实现类MockTimeSource的setTime方法可以调用父类TimeSource的notify方法,并且之前接口TimeSource定义的registerObserver
方法在变为类以后，直接在类中实现了registerObserver方法。这使得MockTimeSource的代码变得非常简洁。

然后作者讲述了一个“故事”，认为TimeSource变为类以后，不得不让一个代码示例中从未出现的Clock类的子类必须同时继承TimeSource和Clock。
因为Clock未出现在作者的代码示例中，所以不知道它是个什么东西，只有一句描述：Clock只是一个知道时间的类，它不应该依赖于注册和更新逻辑。

作者还是给出了C++代码中的多继承示例，让ObservableClock同时继承Clock和TimeSource：
```text
class ObservableClock: public Clock,public TimeSource{
    public:
        virtual void tic(){
            Clock::tic();
            TimeSource::notify(getHours(),getMinutes(),getSeconds());
        }
        virtual void setTime(int hours,int minutes,int seconds){
            Clock::setTime(hours,minutes,seconds);
            TimeSource:notify(hours,minutes,seconds);
        }
}
```
不太懂C++。。。

这里作者发散得太远了，他把TimeSource的notify方法放到另一个类中实现，TimeSource改回接口。总之非常糟糕，不如直接跟着我跳到下一步。

这一步的代码也省了。

#### step4

到这里我思考了下Observer模式的一些内涵：某个对象的状态发生了变化，观察者观察到了这个变化，它把这些变化立即通知给另一个对象。

我们把这个过程进行慢动作分解：

1. 有一个拥有可变属性的对象：Subject；

2. Subject更改了它的一个属性，假设是int state，从0变为1；

3. 观察者Observer观察到了这个变化。“观察到了”意思可能并不是观察者主动观察到的，也可能是Subject通知了Observer；

4. 有一个需要知道Subject变化的对象，姑且称其为Client吧；

5. Client需要知道的可能并不是原始的Subject.state属性，
比如它可能需要知道state代表的数字的文本描述，假设它对应的是Client的String stateDisplay属性；

6. 观察者通过一个方法，假设名字就叫update()方法吧，把Client.stateDisplay属性设置为一段文字描述；

7. Client在不知情的情况下，它的stateDisplay属性值发生了变化。

作者提到了“推模式”和“拉模式”，推模式意思是Subject变化主动让观察者知道，让它update Client.stateDisplay，
拉模式则是Client获取它的stateDisplay时观察者才从Subject获取state进行update。

现在思考另一个重要问题。在7步慢动作分解中，Subject、Observer、Client三者必定存在着某种联系，类之间必定存在
is-a,has-a,use-a中的某一种关系。

Subject、Observer、Client可以是类、接口、抽象类，啥都行，但该如何设计更优呢？

作者把TimeSource拆分了一下，把时分秒这些属性抽取出来变成了需要传递的东西，仍然叫TimeSource，另外把通知观察者的方法放到了新类Subject
中，让它变成了另一个角色：发布者,它单纯用来通知观察者去执行update方法。然后作者让MockTimeSource继承了Subject并实现TimeSource接口。
这样MockTimeSource既是需要传递的东西，又是发布者。另外作者让MockTimeSink实现Observer接口，变成了观察者。

发布者和要传递的东西，以及观察者和Client合二为一，是不是Observer模式的精髓呢？我还不敢肯定，但感觉还是有点道理。

