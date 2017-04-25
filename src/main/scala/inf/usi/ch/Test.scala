package inf.usi.ch


/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {

  val a = "String[] words = instring.split(\"\\\\s+\");\n for (int i = 0; i < words.length; i++) {\n words[i] = words[i].toLowerCase();\n }\n String[] wordsout = new String[50];\n Arrays.fill(wordsout,\"\");\n   int e = 0;\n  for (int i = 0; i < words.length; i++) {\n        if (words[i] != \"\") {\n            wordsout[e] = words[e];\n            wordsout[e] = wordsout[e].replaceAll(\" \", \"\");\n            e++;\n        }\n    }\n    return wordsout;"
  val b = a.replaceAll("[^a-zA-Z0-9 ]"," ")
  println(b)

}
