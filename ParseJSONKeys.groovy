import groovy.json.JsonSlurper
import static groovy.json.JsonOutput.*

//def dumpkeys1(p, o) {
//    if (p.length() == 0) {
//        dumpkeys1('$', o)
//    } else {
//        if (o instanceof java.util.Map) {
//            def keys = o.keySet()
//            keys.each { it -> 
//                if (o[it] instanceof String) {
//                    if(o[it].length() == 0) {
//                        println(p + "." + it + " -- \"\"") 
//                    } else {
//                        println(p + "." + it) 
//                    }
//                } else if (o[it] == null) {
//                    println(p + "." + it + " -- null") 
//                } else if (o[it] instanceof java.util.Map) {
//                    dumpkeys1(p + "." + it, o[it])
//                } else if (o[it] instanceof java.util.ArrayList) {
//                    dumpkeys1(p + "." + it, o[it])
//                } else {
//                    println(p + "." + it + " -- unexpected " + o[it].getClass()) 
//                }
//            }
//        } else if (o instanceof java.util.List) {
//            o.eachWithIndex { it, i -> dumpkeys1(p+"["+i+"]", it) }
//        } else {
//    	println(p + "." + it + " -- unexpected " + o.getClass()) 
//        }
//    }
//}
//def dumpkeys1(o) { dumpkeys1('', o) }

//def dumpkeys2(jsonpathbits, jsonobject) {
//    if (jsonobject instanceof java.util.Map) {
//        def keys = jsonobject.keySet()
//        keys.each { it -> 
//            if (jsonobject[it] instanceof java.util.Map) {
//                dumpkeys2((jsonpathbits + [it]), jsonobject[it])
//            } else if (jsonobject[it] instanceof java.util.ArrayList) {
//                jsonobject[it].eachWithIndex { ait, i ->
//                    dumpkeys2(jsonpathbits + [it+"["+i+"]"], ait)
//                }
//            } else if (jsonobject[it] instanceof String) {
//                if(jsonobject[it].length() == 0) {
//                    println((jsonpathbits + [it]).toString() + " -- \"\"") 
//                } else {
//                    println((jsonpathbits + [it]).toString()) 
//                }
//            } else if (jsonobject[it] == null) {
//                println((jsonpathbits + [it]).toString() + " -- null") 
//            } else {
//                println((jsonpathbits + [it]).toString() + " -- unexpected " + jsonobject[it].getClass()) 
//            }
//        }
//    }
//}
//def dumpkeys2(o) { dumpkeys2(['$'], o) }

def dumpkeys3(jsonpath, jsonpathbits, jsonobject, parseresults) {
    if (jsonobject instanceof java.util.Map) {
        def keys = jsonobject.keySet()
        keys.each { it -> 
            if (jsonobject[it] instanceof java.util.Map) {
                dumpkeys3(jsonpath+"."+it, (jsonpathbits + [it]), jsonobject[it], parseresults)
            } else if (jsonobject[it] instanceof java.util.ArrayList) {
                jsonobject[it].eachWithIndex { ait, i ->
                    dumpkeys3(jsonpath+"."+it+"["+i+"]", jsonpathbits + [it], ait, parseresults)
                }
            } else if (jsonobject[it] instanceof String) {
                if(jsonobject[it].length() == 0) {
                    println(jsonpath + "." + it + " -- \"\"") 
                } else {
                    if (parseresults.containsKey(jsonpath)) {
                        parseresults[jsonpath][2].add(it)
                    } else {
                        parseresults[jsonpath] = [jsonpathbits[-2], jsonpathbits[-1], [it]]
                    }
                    //parseresults[jsonpath] = parseresults.getOrDefault(jsonpath, []) + [it]
                    //println(jsonpath + "." + it + " " + ([jsonpathbits[-1], jsonpathbits[-2]].toString()))
                }
            } else if (jsonobject[it] == null) {
                println(jsonpath + "." + it + " -- null") 
            } else {
                println(jsonpath + "." + it + " -- unexpected " + jsonobject[it].getClass()) 
            }
        }
    }
}

def dumpkeys3(o, parseresults) { dumpkeys3('$', ['$'], o, parseresults) }

if (args.size() < 1) {
    println "Need arguement"
    System.exit(0);
}
File f = new File(args[0])

def jsonSlurper = new JsonSlurper()
j = jsonSlurper.parse(f)

parseresults = [:]
dumpkeys3(j, parseresults)
println prettyPrint(toJson(parseresults))
