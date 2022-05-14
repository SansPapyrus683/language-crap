# language crap
we do a little coding

but anyways, welcome to my lil language!

made it with the help of an online tutorial, you really can learn anything
on the internet these days

but i'll cut the crap and get to some substance

all the code examples i use here can be found
[here](/src/io/github/sanspapyrus683/prog/examples)

## let's start simple
```
print "hello world!";  // prints hello world, what else could it do
```
as you can see, this language is like if java had an affair with python 2
after having one too many beers

print is just `print`, strings are as normal (only double quotes though),
and you have to end statements with a semicolon like in most c-style langs  
i only support single-line comments with the `//` then the comment, sorry

but as you can see, no main function, no main class, just straight code  
i like that in a language

## ok a lil more complex now (variables)
```
var a = 1;
var b = 2;
print a + b;  // outputs 3.0
```
the language just uses java's `double` underneath the hood, so all `int`s
are implicitly converted to doubles

you declare a variable with `var` & the variable name, then set it equal to
the value you want it to have

## control flow moment
### if statements
```
var a = 4;
var b = 2;
var c = 8;
if (a + b == c or a * b == c) {
    print "hooray!";  // this is what runs (aka "hooray" is printed)
} else {
    print "bruh";
}
```
no `else if`s, sorry  
but yeah, `if` and `else` statements are basically the same in those
as java except you use `and` and `or` instead of `&&` and `||`

### it's just a little looping, jack
```
// this just prints the numbers from 1 to 10
for (var i = 0; i < 10; i = i + 1) {
    print i;
}
```
basically the same as java loops except there's no shorthand increment operator  
you have your initial statement, your looping condition, & the post-loop
operator or whatever

## fizzbuzz
yeah here's fizzbuzz to show you all these concepts
```
var upTo = 100;
for (var i = 0; i < upTo; i = i + 1) {
    if (i % 5 == 0 and i % 3 == 0) {
        print "FizzBuzz";
    } else {
        if (i % 5 == 0) {
            print "Fizz";
        } else {
            if (i % 3 == 0) {
                print "Buzz";
            } else {
                print i;
            }
        }
    }
}
```
