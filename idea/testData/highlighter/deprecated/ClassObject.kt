fun test() {
   <info descr="'MyClass.<class-object-for-MyClass>' is deprecated">MyClass</info>.test
   MyClass()
   val a: MyClass? = null
   val b: MyTrait? = null
   <info descr="'MyTrait.<class-object-for-MyTrait>' is deprecated">MyTrait</info>.test
}

class MyClass(): MyTrait {
    Deprecated class object {
        val <info>test</info>: String = ""
    }
}

trait MyTrait {
    Deprecated class object {
        val <info>test</info>: String = ""
    }
}