import groovy.json.JsonSlurper

def dumpkeys(p, o) {
    if (p.length() == 0) {
        dumpkeys('$', o)
    } else {
        if (o instanceof java.util.Map) {
            def keys = o.keySet()
            keys.each { it -> 
                if (o[it] instanceof String) {
                    if(o[it].length() == 0) {
                        println(p + "." + it + " -- \"\"") 
                    } else {
                        println(p + "." + it) 
                    }
                } else if (o[it] == null) {
                    println(p + "." + it + " -- null") 
                } else if (o[it] instanceof java.util.Map) {
                    dumpkeys(p + "." + it, o[it])
                } else if (o[it] instanceof java.util.ArrayList) {
                    dumpkeys(p + "." + it, o[it])
                } else {
                    println(p + "." + it + " -- unexpected " + o[it].getClass()) 
                }
            }
        } else if (o instanceof java.util.List) {
            o.eachWithIndex { it, i -> dumpkeys(p+"["+i+"]", it) }
        } else {
    	println(p + "." + it + " -- unexpected " + o.getClass()) 
        }
    }
}
def dumpkeys(o) { dumpkeys('', o) }

if (args.size() < 1) {
    println "Need arguement"
    System.exit(0);
}
File f = new File(args[0])

def jsonSlurper = new JsonSlurper()
j = jsonSlurper.parse(f)

dumpkeys(j)
