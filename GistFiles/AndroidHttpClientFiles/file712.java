/home/me/jdk9/bin/java -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:41852,suspend=y,server=n -Dfile.encoding=UTF-8 -classpath /home/me/idea/lib/idea_rt.jar:/home/me/idea/plugins/junit/lib/junit-rt.jar:/home/me/jdk9!/java.activation:/home/me/jdk9!/java.annotations.common:/home/me/jdk9!/java.base:/home/me/jdk9!/java.compact1:/home/me/jdk9!/java.compact2:/home/me/jdk9!/java.compact3:/home/me/jdk9!/java.compiler:/home/me/jdk9!/java.corba:/home/me/jdk9!/java.datatransfer:/home/me/jdk9!/java.desktop:/home/me/jdk9!/java.httpclient:/home/me/jdk9!/java.instrument:/home/me/jdk9!/java.jnlp:/home/me/jdk9!/java.logging:/home/me/jdk9!/java.management:/home/me/jdk9!/java.naming:/home/me/jdk9!/java.prefs:/home/me/jdk9!/java.rmi:/home/me/jdk9!/java.scripting:/home/me/jdk9!/java.se:/home/me/jdk9!/java.se.ee:/home/me/jdk9!/java.security.jgss:/home/me/jdk9!/java.security.sasl:/home/me/jdk9!/java.smartcardio:/home/me/jdk9!/java.sql:/home/me/jdk9!/java.sql.rowset:/home/me/jdk9!/java.transaction:/home/me/jdk9!/java.xml:/home/me/jdk9!/java.xml.bind:/home/me/jdk9!/java.xml.crypto:/home/me/jdk9!/java.xml.ws:/home/me/jdk9!/javafx.base:/home/me/jdk9!/javafx.controls:/home/me/jdk9!/javafx.deploy:/home/me/jdk9!/javafx.fxml:/home/me/jdk9!/javafx.graphics:/home/me/jdk9!/javafx.media:/home/me/jdk9!/javafx.swing:/home/me/jdk9!/javafx.web:/home/me/jdk9!/jdk.accessibility:/home/me/jdk9!/jdk.attach:/home/me/jdk9!/jdk.charsets:/home/me/jdk9!/jdk.compiler:/home/me/jdk9!/jdk.crypto.ec:/home/me/jdk9!/jdk.crypto.pkcs11:/home/me/jdk9!/jdk.deploy:/home/me/jdk9!/jdk.deploy.controlpanel:/home/me/jdk9!/jdk.deploy.controlpanel.fx:/home/me/jdk9!/jdk.dynalink:/home/me/jdk9!/jdk.hotspot.agent:/home/me/jdk9!/jdk.httpserver:/home/me/jdk9!/jdk.internal.le:/home/me/jdk9!/jdk.internal.opt:/home/me/jdk9!/jdk.jartool:/home/me/jdk9!/jdk.javadoc:/home/me/jdk9!/jdk.javaws:/home/me/jdk9!/jdk.jcmd:/home/me/jdk9!/jdk.jconsole:/home/me/jdk9!/jdk.jdeps:/home/me/jdk9!/jdk.jdi:/home/me/jdk9!/jdk.jdwp.agent:/home/me/jdk9!/jdk.jfr:/home/me/jdk9!/jdk.jlink:/home/me/jdk9!/jdk.jshell:/home/me/jdk9!/jdk.jsobject:/home/me/jdk9!/jdk.jstatd:/home/me/jdk9!/jdk.jvmstat:/home/me/jdk9!/jdk.localedata:/home/me/jdk9!/jdk.management:/home/me/jdk9!/jdk.naming.dns:/home/me/jdk9!/jdk.naming.rmi:/home/me/jdk9!/jdk.net:/home/me/jdk9!/jdk.pack200:/home/me/jdk9!/jdk.packager:/home/me/jdk9!/jdk.packager.services:/home/me/jdk9!/jdk.plugin:/home/me/jdk9!/jdk.plugin.dom:/home/me/jdk9!/jdk.plugin.server:/home/me/jdk9!/jdk.policytool:/home/me/jdk9!/jdk.rmic:/home/me/jdk9!/jdk.scripting.nashorn:/home/me/jdk9!/jdk.scripting.nashorn.shell:/home/me/jdk9!/jdk.sctp:/home/me/jdk9!/jdk.security.auth:/home/me/jdk9!/jdk.security.jgss:/home/me/jdk9!/jdk.snmp:/home/me/jdk9!/jdk.unsupported:/home/me/jdk9!/jdk.vm.ci:/home/me/jdk9!/jdk.xml.bind:/home/me/jdk9!/jdk.xml.dom:/home/me/jdk9!/jdk.xml.ws:/home/me/jdk9!/jdk.zipfs:/home/me/jdk9/lib/ant-javafx.jar:/home/me/jdk9/lib/deploy.jar:/home/me/jdk9/lib/java.jnlp.jar:/home/me/jdk9/lib/javafx-swt.jar:/home/me/jdk9/lib/javaws.jar:/home/me/jdk9/lib/jdk.deploy.jar:/home/me/jdk9/lib/jdk.javaws.jar:/home/me/jdk9/lib/jdk.plugin.dom.jar:/home/me/jdk9/lib/jdk.plugin.jar:/home/me/jdk9/lib/plugin-legacy.jar:/home/me/jdk9/lib/plugin.jar:/home/me/opennars/app/target/test-classes:/home/me/opennars/app/target/classes:/home/me/opennars/nal/target/classes:/home/me/opennars/logic/target/classes:/home/me/opennars/util/target/classes:/home/me/.m2/repository/org/jetbrains/annotations/15.0/annotations-15.0.jar:/home/me/.m2/repository/org/eclipse/collections/eclipse-collections-api/8.0.0/eclipse-collections-api-8.0.0.jar:/home/me/.m2/repository/net/jcip/jcip-annotations/1.0/jcip-annotations-1.0.jar:/home/me/.m2/repository/org/eclipse/collections/eclipse-collections/8.0.0/eclipse-collections-8.0.0.jar:/home/me/.m2/repository/org/apache/commons/commons-collections4/4.1/commons-collections4-4.1.jar:/home/me/.m2/repository/com/lmax/disruptor/3.3.5/disruptor-3.3.5.jar:/home/me/.m2/repository/com/google/guava/guava/19.0/guava-19.0.jar:/home/me/.m2/repository/org/reflections/reflections/0.9.10/reflections-0.9.10.jar:/home/me/.m2/repository/com/google/code/findbugs/annotations/2.0.1/annotations-2.0.1.jar:/home/me/.m2/repository/org/apache/commons/commons-lang3/3.4/commons-lang3-3.4.jar:/home/me/.m2/repository/org/jgrapht/jgrapht-core/0.9.2/jgrapht-core-0.9.2.jar:/home/me/.m2/repository/org/jgrapht/jgrapht-ext/0.9.2/jgrapht-ext-0.9.2.jar:/home/me/.m2/repository/org/tinyjee/jgraphx/jgraphx/2.0.0.1/jgraphx-2.0.0.1.jar:/home/me/.m2/repository/jgraph/jgraph/5.13.0.0/jgraph-5.13.0.0.jar:/home/me/.m2/repository/org/codehaus/janino/janino/3.0.1/janino-3.0.1.jar:/home/me/.m2/repository/org/codehaus/janino/commons-compiler/3.0.1/commons-compiler-3.0.1.jar:/home/me/.m2/repository/org/ow2/asm/asm-all/6.0_ALPHA/asm-all-6.0_ALPHA.jar:/home/me/.m2/repository/me/qmx/jitescript/jitescript/0.4.0/jitescript-0.4.0.jar:/home/me/.m2/repository/org/javassist/javassist/3.20.0-GA/javassist-3.20.0-GA.jar:/home/me/.m2/repository/net/bytebuddy/byte-buddy/1.4.22/byte-buddy-1.4.22.jar:/home/me/.m2/repository/org/apache/commons/commons-math3/3.2/commons-math3-3.2.jar:/home/me/.m2/repository/org/slf4j/slf4j-api/1.7.21/slf4j-api-1.7.21.jar:/home/me/.m2/repository/ch/qos/logback/logback-classic/1.1.7/logback-classic-1.1.7.jar:/home/me/.m2/repository/ch/qos/logback/logback-core/1.1.7/logback-core-1.1.7.jar:/home/me/.m2/repository/de/ruedigermoeller/fst/2.48/fst-2.48.jar:/home/me/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.8.1/jackson-core-2.8.1.jar:/home/me/.m2/repository/com/cedarsoftware/java-util/1.9.0/java-util-1.9.0.jar:/home/me/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:/home/me/.m2/repository/com/cedarsoftware/json-io/2.5.1/json-io-2.5.1.jar:/home/me/.m2/repository/ognl/ognl/3.1.10/ognl-3.1.10.jar:/home/me/.m2/repository/com/addthis/stream-lib/3.0.0/stream-lib-3.0.0.jar:/home/me/.m2/repository/it/unimi/dsi/fastutil/6.5.7/fastutil-6.5.7.jar:/home/me/.m2/repository/org/objenesis/objenesis/2.4/objenesis-2.4.jar:/home/me/.m2/repository/com/github/ben-manes/caffeine/caffeine/2.3.3/caffeine-2.3.3.jar:/home/me/.m2/repository/org/iq80/snappy/snappy/0.4/snappy-0.4.jar:/home/me/.m2/repository/org/fusesource/jansi/jansi/1.13/jansi-1.13.jar:/home/me/.m2/repository/junit/junit/4.12/junit-4.12.jar:/home/me/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/me/.m2/repository/com/github/fge/grappa/2.1.0-beta.3/grappa-2.1.0-beta.3.jar:/home/me/.m2/repository/org/ow2/asm/asm-debug-all/5.0.4/asm-debug-all-5.0.4.jar:/home/me/.m2/repository/jline/jline/3.0.0.M1/jline-3.0.0.M1.jar:/home/me/.m2/repository/com/github/drapostolos/type-parser/0.6.0/type-parser-0.6.0.jar:/home/me/.m2/repository/org/semanticweb/yars/nxparser-parsers/2.3.3/nxparser-parsers-2.3.3.jar:/home/me/.m2/repository/org/semanticweb/yars/nxparser-api/2.3.3/nxparser-api-2.3.3.jar:/home/me/.m2/repository/org/semanticweb/yars/nxparser-model/2.3.3/nxparser-model-2.3.3.jar:/home/me/.m2/repository/org/semanticweb/yars/nxparser-commons/2.3.3/nxparser-commons-2.3.3.jar:/home/me/.m2/repository/org/apache/commons/commons-vfs2/2.1/commons-vfs2-2.1.jar:/home/me/.m2/repository/org/xeustechnologies/jcl-core/2.7/jcl-core-2.7.jar:/home/me/.m2/repository/cglib/cglib-nodep/2.2/cglib-nodep-2.2.jar:/home/me/.m2/repository/com/googlecode/concurrent-trees/concurrent-trees/2.6.0/concurrent-trees-2.6.0.jar:/home/me/.m2/repository/com/eclipsesource/minimal-json/minimal-json/0.9.4/minimal-json-0.9.4.jar:/home/me/.m2/repository/org/infinispan/infinispan-embedded/9.0.0.Alpha4/infinispan-embedded-9.0.0.Alpha4.jar:/home/me/.m2/repository/org/jboss/spec/javax/transaction/jboss-transaction-api_1.1_spec/1.0.1.Final/jboss-transaction-api_1.1_spec-1.0.1.Final.jar:/home/me/.m2/repository/org/jfxvnc/jfxvnc-net/1.0.1/jfxvnc-net-1.0.1.jar:/home/me/.m2/repository/io/netty/netty-handler/4.1.5.Final/netty-handler-4.1.5.Final.jar:/home/me/.m2/repository/io/netty/netty-buffer/4.1.5.Final/netty-buffer-4.1.5.Final.jar:/home/me/.m2/repository/io/netty/netty-common/4.1.5.Final/netty-common-4.1.5.Final.jar:/home/me/.m2/repository/io/netty/netty-transport/4.1.5.Final/netty-transport-4.1.5.Final.jar:/home/me/.m2/repository/io/netty/netty-resolver/4.1.5.Final/netty-resolver-4.1.5.Final.jar:/home/me/.m2/repository/io/netty/netty-codec/4.1.5.Final/netty-codec-4.1.5.Final.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt-main/2.3.2/gluegen-rt-main-2.3.2.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-android-aarch64.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-android-armv6.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-linux-amd64.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-linux-armv6.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-linux-armv6hf.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-linux-i586.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-macosx-universal.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-solaris-amd64.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-solaris-i586.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-windows-amd64.jar:/home/me/.m2/repository/org/jogamp/gluegen/gluegen-rt/2.3.2/gluegen-rt-2.3.2-natives-windows-i586.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all-noawt-main/2.3.2/jogl-all-noawt-main-2.3.2.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all-noawt/2.3.2/jogl-all-noawt-2.3.2.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-android-aarch64.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-android-armv6.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-linux-amd64.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-linux-armv6.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-linux-armv6hf.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-linux-i586.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-macosx-universal.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-solaris-amd64.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-solaris-i586.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-windows-amd64.jar:/home/me/.m2/repository/org/jogamp/jogl/jogl-all/2.3.2/jogl-all-2.3.2-natives-windows-i586.jar:/home/me/.m2/repository/de/dfki/mary/voice-cmu-slt-hsmm/5.2/voice-cmu-slt-hsmm-5.2.jar:/home/me/.m2/repository/de/dfki/mary/marytts-common/5.2/marytts-common-5.2.jar:/home/me/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:/home/me/.m2/repository/commons-io/commons-io/2.5/commons-io-2.5.jar:/home/me/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar:/home/me/.m2/repository/com/twmacinta/fast-md5/2.7.1/fast-md5-2.7.1.jar:/home/me/.m2/repository/org/codehaus/groovy/groovy-all/2.4.6/groovy-all-2.4.6.jar:/home/me/.m2/repository/de/dfki/mary/marytts-runtime/5.2/marytts-runtime-5.2.jar:/home/me/.m2/repository/de/dfki/mary/marytts-signalproc/5.2/marytts-signalproc-5.2.jar:/home/me/.m2/repository/gov/nist/math/Jampack/1.0/Jampack-1.0.jar:/home/me/.m2/repository/gov/nist/math/jama/1.0.3/jama-1.0.3.jar:/home/me/.m2/repository/org/swinglabs/swing-layout/1.0.3/swing-layout-1.0.3.jar:/home/me/.m2/repository/com/ibm/icu/icu4j/54.1.1/icu4j-54.1.1.jar:/home/me/.m2/repository/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar:/home/me/.m2/repository/de/dfki/mary/emotionml-checker-java/1.1/emotionml-checker-java-1.1.jar:/home/me/.m2/repository/de/dfki/lt/jtok/jtok-core/1.9.3/jtok-core-1.9.3.jar:/home/me/.m2/repository/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar:/home/me/.m2/repository/net/sf/trove4j/trove4j/2.0.2/trove4j-2.0.2.jar:/home/me/.m2/repository/org/apache/httpcomponents/httpcore/4.1/httpcore-4.1.jar:/home/me/.m2/repository/org/apache/httpcomponents/httpcore-nio/4.1/httpcore-nio-4.1.jar:/home/me/.m2/repository/org/apache/opennlp/opennlp-maxent/3.0.3/opennlp-maxent-3.0.3.jar:/home/me/.m2/repository/org/apache/opennlp/opennlp-tools/1.5.3/opennlp-tools-1.5.3.jar:/home/me/.m2/repository/net/sf/jwordnet/jwnl/1.3.3/jwnl-1.3.3.jar:/home/me/.m2/repository/org/hsqldb/hsqldb/2.0.0/hsqldb-2.0.0.jar:/home/me/.m2/repository/de/dfki/mary/marytts-lang-en/5.2/marytts-lang-en-5.2.jar:/home/me/.m2/repository/edu/cmu/sphinx/sphinx4-core/5prealpha-SNAPSHOT/sphinx4-core-5prealpha-SNAPSHOT.jar:/home/me/.m2/repository/edu/cmu/sphinx/sphinx4-data/5prealpha-SNAPSHOT/sphinx4-data-5prealpha-SNAPSHOT.jar:/home/me/.m2/repository/org/boofcv/visualize/0.24.1/visualize-0.24.1.jar:/home/me/.m2/repository/org/boofcv/ip/0.24.1/ip-0.24.1.jar:/home/me/.m2/repository/org/georegression/georegression/0.11/georegression-0.11.jar:/home/me/.m2/repository/org/boofcv/geo/0.24.1/geo-0.24.1.jar:/home/me/.m2/repository/org/boofcv/feature/0.24.1/feature-0.24.1.jar:/home/me/.m2/repository/org/boofcv/io/0.24.1/io-0.24.1.jar:/home/me/.m2/repository/com/thoughtworks/xstream/xstream/1.4.7/xstream-1.4.7.jar:/home/me/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar:/home/me/.m2/repository/xpp3/xpp3_min/1.1.4c/xpp3_min-1.1.4c.jar:/home/me/.m2/repository/org/boofcv/recognition/0.24.1/recognition-0.24.1.jar:/home/me/.m2/repository/org/boofcv/sfm/0.24.1/sfm-0.24.1.jar:/home/me/.m2/repository/org/boofcv/calibration/0.24.1/calibration-0.24.1.jar:/home/me/.m2/repository/org/ddogleg/ddogleg/0.9/ddogleg-0.9.jar:/home/me/.m2/repository/org/ejml/simple/0.29/simple-0.29.jar:/home/me/.m2/repository/org/ejml/core/0.29/core-0.29.jar:/home/me/.m2/repository/org/ejml/dense64/0.29/dense64-0.29.jar:/home/me/.m2/repository/org/ejml/equation/0.29/equation-0.29.jar:/home/me/.m2/repository/org/boofcv/WebcamCapture/0.24.1/WebcamCapture-0.24.1.jar:/home/me/.m2/repository/com/github/sarxos/webcam-capture/0.3.11/webcam-capture-0.3.11.jar:/home/me/.m2/repository/com/nativelibs4java/bridj/0.7.0/bridj-0.7.0.jar com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 nars.rdfowl.NQuadsRDFTest,testSchema1
Connected to the target VM, address: '127.0.0.1:41852', transport: 'socket'
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/home/me/.m2/repository/ch/qos/logback/logback-classic/1.1.7/logback-classic-1.1.7.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/home/me/.m2/repository/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [ch.qos.logback.classic.util.ContextSelectorStaticBinder]
( $,NQuadsRDF ): "loading /tmp/all-layers.nq".
( $,PremiseRuleSet ): "cache loaded /tmp/PremiseRuleSet_default.meta.nal_1473708536000_36091: (41299 bytes, from Tue Sep 13 06:37:08 EDT 2016)".
( $,PremiseRuleSet ): "indexed 244 total rules, consisting of 136 unique pattern components terms".
( $,Deriver ): "Rule parse (1135 ms)".
( $,Deriver ): "Rule compile (152 ms)".
  TaskProcess: $.50;.50;.50$ (Bacteria<->Pharmacy)? {1: ~ï} Narsese
               $.06;.36;.25$ (Bacteria-->MedicalOrganization)? {4: Fc;;}
               $.01;.33;.23$ (InfectiousAgentClass-->MedicalOrganization)? {4: 4x;;}
               $.01;.33;.23$ (MedicalOrganization-->InfectiousAgentClass)? {4: 4x;;}
               $.01;.33;.23$ (InfectiousAgentClass<->MedicalOrganization)? {4: 4x;;}
               $0.0;.34;.22$ (MulticellularParasite-->MedicalOrganization)? {6: 5â;;}
               $0.0;.44;.25$ ((VeterinaryCare-->$1)==>(DiagnosticLab-->$1)). %1.0;.45% {7: 7F;aç}
               $0.0;.44;.25$ ((DiagnosticLab-->$1)<=>(VeterinaryCare-->$1)). %1.0;.45% {7: 7F;aç}
               $0.0;.44;.25$ ((DiagnosticLab-->$1)==>(VeterinaryCare-->$1)). %1.0;.45% {7: 7F;aç}
               $0.0;.44;.44$ ((DiagnosticLab-->#1)&&(VeterinaryCare-->#1)). %1.0;.81% {7: 7F;aç}
               $0.0;.53;.40$ (VeterinaryCare-->DiagnosticLab)? {7: aç;;}
               $0.0;.53;.30$ (VeterinaryCare-->DiagnosticLab). %1.0;.45% {7: 7F;aç}
               $0.0;.53;.30$ (DiagnosticLab<->VeterinaryCare). %1.0;.45% {7: 7F;aç}
               $0.0;.44;.44$ ((DiagnosticLab|VeterinaryCare)-->MedicalOrganization). %1.0;.81% {7: 7F;aç}
               $0.0;.44;.44$ ((DiagnosticLab&VeterinaryCare)-->MedicalOrganization). %1.0;.81% {7: 7F;aç}
               $0.0;.44;.44$ ((VeterinaryCare~DiagnosticLab)-->MedicalOrganization). %0.0;.81% {7: 7F;aç}
               $0.0;.53;.40$ (DiagnosticLab<->VeterinaryCare)? {7: aç;;}
               $0.0;.18;.20$ ((VeterinaryCare~DiagnosticLab)-->InfectiousAgentClass)? {8: 7F;aç;;}
               $0.0;.18;.20$ (InfectiousAgentClass<->(VeterinaryCare~DiagnosticLab))? {8: 7F;aç;;}
               $0.0;.35;.21$ ((Pharmacy-->$1)==>(--,((VeterinaryCare~DiagnosticLab)-->$1))). %1.0;.42% {8: 7F;aç;Fc}
               $0.0;.35;.21$ ((--,((VeterinaryCare~DiagnosticLab)-->$1))==>(Pharmacy-->$1)). %1.0;.42% {8: 7F;aç;Fc}
               $0.0;.35;.21$ ((--,((VeterinaryCare~DiagnosticLab)-->$1))<=>(Pharmacy-->$1)). %1.0;.42% {8: 7F;aç;Fc}
               $0.0;.38;.39$ ((Pharmacy-->#1)&&((VeterinaryCare~DiagnosticLab)-->#1)). %0.0;.73% {8: 7F;aç;Fc}
               $0.0;.44;.29$ ((VeterinaryCare~DiagnosticLab)-->Pharmacy)? {8: 7F;aç;;}
               $0.0;.44;.26$ (Pharmacy<->(VeterinaryCare~DiagnosticLab)). %0.0;.42% {8: 7F;aç;Fc}
               $0.0;.38;.39$ ((Pharmacy|(VeterinaryCare~DiagnosticLab))-->MedicalOrganization). %0.0;.73% {8: 7F;aç;Fc}
               $0.0;.38;.39$ ((Pharmacy&(VeterinaryCare~DiagnosticLab))-->MedicalOrganization). %1.0;.73% {8: 7F;aç;Fc}
               $0.0;.38;.39$ (((VeterinaryCare~DiagnosticLab)~Pharmacy)-->MedicalOrganization). %0.0;.73% {8: 7F;aç;Fc}
               $0.0;.44;.29$ (Pharmacy<->(VeterinaryCare~DiagnosticLab))? {8: 7F;aç;;}
               $0.0;.34;.22$ (Fungus-->MedicalOrganization)? {8: bl;;}
               $.01;.33;.23$ (DiagnosticLab-->Bacteria)? {9: 7F;;}
               $.01;.33;.23$ (Bacteria<->DiagnosticLab)? {9: 7F;;}
               $0.0;.49;.25$ ((DiagnosticLab-->$1)==>(Hospital-->$1)). %1.0;.45% {9: 7F;rò}
               $0.0;.49;.25$ ((DiagnosticLab-->$1)<=>(Hospital-->$1)). %1.0;.45% {9: 7F;rò}
               $0.0;.49;.25$ ((Hospital-->$1)==>(DiagnosticLab-->$1)). %1.0;.45% {9: 7F;rò}
               $0.0;.49;.44$ ((DiagnosticLab-->#1)&&(Hospital-->#1)). %1.0;.81% {9: 7F;rò}
               $0.0;.60;.33$ (DiagnosticLab-->Hospital)? {9: 7F;;}
               $0.0;.60;.30$ (DiagnosticLab-->Hospital). %1.0;.45% {9: 7F;rò}
               $0.0;.60;.30$ (DiagnosticLab<->Hospital). %1.0;.45% {9: 7F;rò}
               $0.0;.49;.44$ ((DiagnosticLab|Hospital)-->MedicalOrganization). %1.0;.81% {9: 7F;rò}
               $0.0;.49;.44$ ((DiagnosticLab&Hospital)-->MedicalOrganization). %1.0;.81% {9: 7F;rò}
               $0.0;.49;.44$ ((DiagnosticLab~Hospital)-->MedicalOrganization). %0.0;.81% {9: 7F;rò}
               $0.0;.60;.33$ (DiagnosticLab<->Hospital)? {9: 7F;;}
               $.01;.27;.29$ (VeterinaryCare-->Bacteria)? {9: aç;;}
               $.01;.27;.29$ (Bacteria<->VeterinaryCare)? {9: aç;;}
               $0.0;.44;.25$ ((VeterinaryCare-->$1)==>(Hospital-->$1)). %1.0;.45% {9: aç;rò}
               $0.0;.44;.25$ ((Hospital-->$1)<=>(VeterinaryCare-->$1)). %1.0;.45% {9: aç;rò}
               $0.0;.44;.25$ ((Hospital-->$1)==>(VeterinaryCare-->$1)). %1.0;.45% {9: aç;rò}
               $0.0;.44;.44$ ((Hospital-->#1)&&(VeterinaryCare-->#1)). %1.0;.81% {9: aç;rò}
               $0.0;.53;.40$ (VeterinaryCare-->Hospital)? {9: aç;;}
               $0.0;.53;.30$ (VeterinaryCare-->Hospital). %1.0;.45% {9: aç;rò}
               $0.0;.53;.30$ (Hospital<->VeterinaryCare). %1.0;.45% {9: aç;rò}
               $0.0;.44;.44$ ((Hospital|VeterinaryCare)-->MedicalOrganization). %1.0;.81% {9: aç;rò}
               $0.0;.44;.44$ ((Hospital&VeterinaryCare)-->MedicalOrganization). %1.0;.81% {9: aç;rò}
               $0.0;.44;.44$ ((VeterinaryCare~Hospital)-->MedicalOrganization). %0.0;.81% {9: aç;rò}
               $0.0;.53;.40$ (Hospital<->VeterinaryCare)? {9: aç;;}
               $0.0;.49;.25$ ((Fungus-->$1)==>(Bacteria-->$1)). %1.0;.45% {9: 4x;bl}
               $0.0;.49;.25$ ((Bacteria-->$1)<=>(Fungus-->$1)). %1.0;.45% {9: 4x;bl}
               $0.0;.49;.25$ ((Bacteria-->$1)==>(Fungus-->$1)). %1.0;.45% {9: 4x;bl}
               $0.0;.49;.44$ ((Bacteria-->#1)&&(Fungus-->#1)). %1.0;.81% {9: 4x;bl}
               $0.0;.60;.33$ (Fungus-->Bacteria)? {9: bl;;}
               $0.0;.60;.30$ (Fungus-->Bacteria). %1.0;.45% {9: 4x;bl}
               $0.0;.60;.30$ (Bacteria<->Fungus). %1.0;.45% {9: 4x;bl}
               $0.0;.49;.44$ ((Bacteria|Fungus)-->InfectiousAgentClass). %1.0;.81% {9: 4x;bl}
               $0.0;.49;.44$ ((Bacteria&Fungus)-->InfectiousAgentClass). %1.0;.81% {9: 4x;bl}
               $0.0;.49;.44$ ((Fungus~Bacteria)-->InfectiousAgentClass). %0.0;.81% {9: 4x;bl}
               $0.0;.60;.33$ (Bacteria<->Fungus)? {9: bl;;}
               $0.0;.22;.33$ (Bacteria-->#1)? {9: 4x;bl;;}
               $0.0;.22;.33$ (#1-->Bacteria)? {9: 4x;bl;;}
               $0.0;.22;.33$ (Bacteria<->#1)? {9: 4x;bl;;}
               $0.0;.16;.29$ ((Hospital-->$1)==>(VeterinaryCare-->$1))? {9: aç;rò;;}
               $0.0;.43;.31$ ((Bacteria&Fungus)-->Fungus)? {10: 4x;bl;;}
               $0.0;.43;.31$ (Fungus<->(Bacteria&Fungus))? {10: 4x;bl;;}
               $0.0;.14;.24$ ((--,((VeterinaryCare~DiagnosticLab)-->$1))==>(Pharmacy-->$1))? {10: 7F;aç;Fc;;}
               $0.0;.32;.52$ (VeterinaryCare-->#1). %1.0;.73% {10: aç;rò;;}
               $0.0;.47;.29$ (DiagnosticLab-->CivicStructure). %1.0;.40% {10: 7F;lS;rò}
               $0.0;.34;.15$ (((#1-->CivicStructure)&&(#1-->$2))==>(DiagnosticLab-->$2)). %1.0;.29% {10: 7F;lS;rò}
               $.01;.24;.22$ ((Hospital-->$1)==>((DiagnosticLab|VeterinaryCare)-->$1)). %1.0;.42% {11: 7F;aç;rò}
               $.01;.24;.22$ ((Hospital-->$1)<=>((DiagnosticLab|VeterinaryCare)-->$1)). %1.0;.42% {11: 7F;aç;rò}
               $.01;.24;.22$ (((DiagnosticLab|VeterinaryCare)-->$1)==>(Hospital-->$1)). %1.0;.42% {11: 7F;aç;rò}
               $.01;.24;.39$ ((Hospital-->#1)&&((DiagnosticLab|VeterinaryCare)-->#1)). %1.0;.73% {11: 7F;aç;rò}
               $.01;.28;.35$ (Hospital-->(DiagnosticLab|VeterinaryCare))? {11: rò;;}
               $.01;.28;.26$ (Hospital-->(DiagnosticLab|VeterinaryCare)). %1.0;.42% {11: 7F;aç;rò}
               $.01;.28;.26$ (Hospital<->(DiagnosticLab|VeterinaryCare)). %1.0;.42% {11: 7F;aç;rò}
               $.01;.26;.42$ ((|,DiagnosticLab,Hospital,VeterinaryCare)-->MedicalOrganization). %1.0;.73% {11: 7F;aç;rò}
               $.01;.24;.39$ ((Hospital&(DiagnosticLab|VeterinaryCare))-->MedicalOrganization). %1.0;.73% {11: 7F;aç;rò}
               $.01;.24;.39$ ((Hospital~(DiagnosticLab|VeterinaryCare))-->MedicalOrganization). %0.0;.73% {11: 7F;aç;rò}
               $0.0;.28;.35$ (Hospital<->(DiagnosticLab|VeterinaryCare))? {11: rò;;}
               $0.0;.27;.22$ ((MedicalClinic-->$1)==>((DiagnosticLab|VeterinaryCare)-->$1)). %1.0;.42% {11: 7F;98;aç}
               $0.0;.27;.22$ ((MedicalClinic-->$1)<=>((DiagnosticLab|VeterinaryCare)-->$1)). %1.0;.42% {11: 7F;98;aç}
               $0.0;.27;.22$ (((DiagnosticLab|VeterinaryCare)-->$1)==>(MedicalClinic-->$1)). %1.0;.42% {11: 7F;98;aç}
               $0.0;.27;.39$ ((MedicalClinic-->#1)&&((DiagnosticLab|VeterinaryCare)-->#1)). %1.0;.73% {11: 7F;98;aç}
               $0.0;.32;.28$ (MedicalClinic-->(DiagnosticLab|VeterinaryCare))? {11: 98;;}
               $0.0;.32;.26$ (MedicalClinic-->(DiagnosticLab|VeterinaryCare)). %1.0;.42% {11: 7F;98;aç}
               $0.0;.32;.26$ (MedicalClinic<->(DiagnosticLab|VeterinaryCare)). %1.0;.42% {11: 7F;98;aç}
               $0.0;.29;.42$ ((|,DiagnosticLab,MedicalClinic,VeterinaryCare)-->MedicalOrganization). %1.0;.73% {11: 7F;98;aç}
               $0.0;.27;.39$ ((MedicalClinic&(DiagnosticLab|VeterinaryCare))-->MedicalOrganization). %1.0;.73% {11: 7F;98;aç}
               $0.0;.27;.39$ ((MedicalClinic~(DiagnosticLab|VeterinaryCare))-->MedicalOrganization). %0.0;.73% {11: 7F;98;aç}
               $0.0;.32;.28$ (MedicalClinic<->(DiagnosticLab|VeterinaryCare))? {11: 98;;}
               $0.0;.32;.52$ (DiagnosticLab-->#1). %1.0;.73% {11: 7F;rò;;}
               $0.0;.43;.31$ (Fungus-->(Bacteria&Fungus))? {13: bl;;}
               $0.0;.43;.31$ (Fungus<->(Bacteria&Fungus))? {13: bl;;}
               $0.0;.27;.46$ (VeterinaryCare-->MedicalOrganization). %1.0;.66% {13: 7F;aç;rò;;}
               $0.0;.27;.46$ (DiagnosticLab-->MedicalOrganization). %1.0;.66% {13: 7F;aç;rò;;}
               $0.0;.17;.19$ (MedicalClinic-->(DiagnosticLab|VeterinaryCare)). %1.0;.38% {13: 7F;98;aç;;}
               $.01;.25;.42$ ((DiagnosticLab|VeterinaryCare)-->#1). %1.0;.66% {14: 7F;aç;rò;;}
               $0.0;.17;.19$ ((MedicalClinic-->$1)==>(Hospital-->$1))? {14: 7F;98;aç;;}
               $0.0;.16;.31$ ((DiagnosticLab-->$1)==>(VeterinaryCare-->$1))? {14: aç;rò;;}
               $0.0;.16;.31$ ((DiagnosticLab-->$1)<=>(VeterinaryCare-->$1))? {14: aç;rò;;}
               $0.0;.34;.57$ (Hospital-->#1). %1.0;.73% {14: aç;rò;;}
               $0.0;.26;.27$ (Fungus-->InfectiousAgentClass)? {15: 4x;bl;;}
               $0.0;.16;.33$ ((Bacteria-->$1)==>(Fungus-->$1))? {16: 4x;bl;;}
               $0.0;.37;.29$ (Zoo-->#1)? {16: gh;;}
               $0.0;.37;.29$ (Zoo<->#1)? {16: gh;;}
               $0.0;.41;.25$ ((Zoo-->$1)==>(Hospital-->$1)). %1.0;.45% {16: gh;lS}
               $0.0;.41;.25$ ((Hospital-->$1)<=>(Zoo-->$1)). %1.0;.45% {16: gh;lS}
               $0.0;.41;.25$ ((Hospital-->$1)==>(Zoo-->$1)). %1.0;.45% {16: gh;lS}
               $0.0;.41;.44$ ((Hospital-->#1)&&(Zoo-->#1)). %1.0;.81% {16: gh;lS}
               $0.0;.50;.43$ (Zoo-->Hospital)? {16: gh;;}
               $0.0;.50;.30$ (Zoo-->Hospital). %1.0;.45% {16: gh;lS}
               $0.0;.50;.30$ (Hospital<->Zoo). %1.0;.45% {16: gh;lS}
               $0.0;.41;.44$ ((Hospital|Zoo)-->CivicStructure). %1.0;.81% {16: gh;lS}
               $0.0;.41;.44$ ((Hospital&Zoo)-->CivicStructure). %1.0;.81% {16: gh;lS}
               $0.0;.41;.44$ ((Zoo~Hospital)-->CivicStructure). %0.0;.81% {16: gh;lS}
               $0.0;.50;.43$ (Hospital<->Zoo)? {16: gh;;}
               $0.0;.13;.25$ ((VeterinaryCare-->$1)==>((#2-->CivicStructure)&&(#2-->$3)))? {17: 7F;aç;;}
               $0.0;.13;.25$ (((#1-->CivicStructure)&&(#1-->$2))==>(VeterinaryCare-->$3))? {17: 7F;aç;;}
               $0.0;.13;.25$ ((VeterinaryCare-->$1)<=>((#2-->CivicStructure)&&(#2-->$3)))? {17: 7F;aç;;}
               $0.0;.41;.25$ ((Museum-->$1)==>(Playground-->$1)). %1.0;.45% {19: lá;Ib}
               $0.0;.41;.25$ ((Museum-->$1)<=>(Playground-->$1)). %1.0;.45% {19: lá;Ib}
               $0.0;.41;.25$ ((Playground-->$1)==>(Museum-->$1)). %1.0;.45% {19: lá;Ib}
               $0.0;.41;.44$ ((Museum-->#1)&&(Playground-->#1)). %1.0;.81% {19: lá;Ib}
               $0.0;.50;.43$ (Museum-->Playground)? {19: lá;;}
               $0.0;.50;.30$ (Museum-->Playground). %1.0;.45% {19: lá;Ib}
               $0.0;.50;.30$ (Museum<->Playground). %1.0;.45% {19: lá;Ib}
               $0.0;.41;.44$ ((Museum|Playground)-->CivicStructure). %1.0;.81% {19: lá;Ib}
               $0.0;.41;.44$ ((Museum&Playground)-->CivicStructure). %1.0;.81% {19: lá;Ib}
               $0.0;.41;.44$ ((Museum~Playground)-->CivicStructure). %0.0;.81% {19: lá;Ib}
               $0.0;.50;.43$ (Museum<->Playground)? {19: lá;;}
               $0.0;.33;.22$ ((Museum-->$1)==>((Zoo~Hospital)-->$1)). %0.0;.42% {19: gh;lS;lá}
               $0.0;.33;.22$ ((Museum-->$1)<=>((Zoo~Hospital)-->$1)). %0.0;.42% {19: gh;lS;lá}
               $0.0;.33;.39$ ((Museum-->#1)&&((Zoo~Hospital)-->#1)). %0.0;.73% {19: gh;lS;lá}
               $0.0;.38;.34$ (Museum-->(Zoo~Hospital))? {19: lá;;}
               $0.0;.38;.26$ (Museum-->(Zoo~Hospital)). %0.0;.42% {19: gh;lS;lá}
               $0.0;.38;.26$ (Museum<->(Zoo~Hospital)). %0.0;.42% {19: gh;lS;lá}
               $0.0;.33;.39$ ((Museum|(Zoo~Hospital))-->CivicStructure). %0.0;.73% {19: gh;lS;lá}
               $0.0;.33;.39$ ((Museum&(Zoo~Hospital))-->CivicStructure). %1.0;.73% {19: gh;lS;lá}
               $0.0;.33;.39$ ((Museum~(Zoo~Hospital))-->CivicStructure). %1.0;.73% {19: gh;lS;lá}
               $0.0;.38;.34$ (Museum<->(Zoo~Hospital))? {19: lá;;}
               $0.0;.17;.18$ ((MedicalClinic-->$1)==>((DiagnosticLab|VeterinaryCare)-->$1))? {20: 7F;98;aç;;}
               $0.0;.24;.29$ (InfectiousAgentClass-->Fungus)? {20: 4x;bl;;}
               $0.0;.16;.30$ ((DiagnosticLab-->$1)==>(Zoo-->$1))? {21: 7F;rò;;}
               $0.0;.16;.13$ ((DiagnosticLab-->$1)==>(Zoo-->$1)). %1.0;.20% {21: 7F;gh;lS;rò}
               $0.0;.12;.10$ ((||,(DiagnosticLab-->$1),(Zoo-->$1))==>(Hospital-->$1)). %1.0;.20% {21: 7F;gh;lS;rò}
               $0.0;.14;.11$ (((DiagnosticLab-->$1)&&(Zoo-->$1))==>(Hospital-->$1)). %1.0;.20% {21: 7F;gh;lS;rò}
               $0.0;.16;.11$ ((Zoo-->$1)==>(DiagnosticLab-->$1)). %1.0;.17% {21: 7F;gh;lS;rò}
               $0.0;.16;.11$ ((DiagnosticLab-->$1)<=>(Zoo-->$1)). %1.0;.17% {21: 7F;gh;lS;rò}
               $0.0;.16;.30$ ((Zoo-->$1)==>(DiagnosticLab-->$1))? {21: 7F;rò;;}
               $0.0;.16;.30$ ((DiagnosticLab-->$1)<=>(Zoo-->$1))? {21: 7F;rò;;}
               $0.0;.17;.19$ ((Hospital-->$1)<=>(MedicalClinic-->$1))? {21: 7F;aç;rò;;}
               $0.0;.15;.24$ (((Zoo~Hospital)-->$1)==>(Playground-->$1))? {21: gh;lS;lá;;}
               $0.0;.15;.24$ ((Playground-->$1)==>((Zoo~Hospital)-->$1))? {21: gh;lS;lá;;}
               $0.0;.28;.46$ (Bacteria-->InfectiousAgentClass). %1.0;.73% {21: 4x;bl;;}
               $0.0;.28;.46$ (Fungus-->InfectiousAgentClass). %1.0;.73% {21: 4x;bl;;}
               $0.0;.24;.21$ ((Museum|(Zoo~Hospital))-->(Zoo~Hospital))? {24: gh;lS;lá;;}
               $0.0;.24;.21$ ((Museum|(Zoo~Hospital))<->(Zoo~Hospital))? {24: gh;lS;lá;;}
               $0.0;.16;.20$ ((Hospital-->$1)==>(DiagnosticLab-->$1))? {24: gh;lS;;}
               $0.0;.36;.21$ ((TaxiStand-->$1)==>(--,((Museum~Playground)-->$1))). %1.0;.42% {25: lá;t4;Ib}
               $0.0;.36;.21$ ((--,((Museum~Playground)-->$1))==>(TaxiStand-->$1)). %1.0;.42% {25: lá;t4;Ib}
               $0.0;.36;.21$ ((--,((Museum~Playground)-->$1))<=>(TaxiStand-->$1)). %1.0;.42% {25: lá;t4;Ib}
               $0.0;.38;.39$ ((TaxiStand-->#1)&&((Museum~Playground)-->#1)). %0.0;.73% {25: lá;t4;Ib}
               $0.0;.44;.28$ ((Museum~Playground)-->TaxiStand)? {25: lá;Ib;;}
               $0.0;.44;.26$ (TaxiStand<->(Museum~Playground)). %0.0;.42% {25: lá;t4;Ib}
               $0.0;.38;.39$ ((TaxiStand|(Museum~Playground))-->CivicStructure). %0.0;.73% {25: lá;t4;Ib}
               $0.0;.38;.39$ ((TaxiStand&(Museum~Playground))-->CivicStructure). %1.0;.73% {25: lá;t4;Ib}
               $0.0;.38;.39$ (((Museum~Playground)~TaxiStand)-->CivicStructure). %0.0;.73% {25: lá;t4;Ib}
               $0.0;.44;.28$ (TaxiStand<->(Museum~Playground))? {25: lá;Ib;;}
               $0.0;.56;.37$ (Place-->TaxiStand)? {25: jË;;}
               $0.0;.56;.37$ (TaxiStand-->Place)? {25: jË;;}
               $0.0;.28;.46$ (Museum-->CivicStructure). %1.0;.73% {26: lá;Ib;;}
               $0.0;.28;.46$ (Playground-->CivicStructure). %1.0;.73% {26: lá;Ib;;}
               $0.0;.32;.52$ (Museum-->#1). %1.0;.73% {26: lá;Ib;;}
               $0.0;.33;.55$ (Hospital-->#1). %1.0;.84% {26: aç;gh;lS;rò} Insertion Revision
               $0.0;.32;.52$ (Hospital-->#1). %1.0;.73% {26: gh;lS;;}
               $0.0;.19;.17$ ((Zoo~Hospital)-->CivicStructure)? {27: gh;lS;lá;;}
               $0.0;.13;.15$ ((Museum-->$1)==>(Playground-->$1))? {27: gh;lS;lá;;}
               $0.0;.13;.15$ ((Playground-->$1)==>(Museum-->$1))? {27: gh;lS;lá;;}
               $0.0;.13;.15$ ((Museum-->$1)<=>(Playground-->$1))? {27: gh;lS;lá;;}
               $0.0;.27;.46$ (Museum-->CivicStructure). %1.0;.66% {27: gh;lS;lá;;}
               $0.0;.24;.25$ ((Protozoa-->$1)==>(Bacteria-->$1)). %1.0;.45% {28: 2à;4x}
               $0.0;.24;.25$ ((Bacteria-->$1)<=>(Protozoa-->$1)). %1.0;.45% {28: 2à;4x}
               $0.0;.24;.25$ ((Bacteria-->$1)==>(Protozoa-->$1)). %1.0;.45% {28: 2à;4x}
               $0.0;.24;.44$ ((Bacteria-->#1)&&(Protozoa-->#1)). %1.0;.81% {28: 2à;4x}
               $0.0;.30;.46$ (Protozoa-->Bacteria)? {28: 2à;;}
               $0.0;.30;.30$ (Protozoa-->Bacteria). %1.0;.45% {28: 2à;4x}
               $0.0;.30;.30$ (Bacteria<->Protozoa). %1.0;.45% {28: 2à;4x}
               $0.0;.24;.44$ ((Bacteria|Protozoa)-->InfectiousAgentClass). %1.0;.81% {28: 2à;4x}
               $0.0;.24;.44$ ((Bacteria&Protozoa)-->InfectiousAgentClass). %1.0;.81% {28: 2à;4x}
               $0.0;.24;.44$ ((Protozoa~Bacteria)-->InfectiousAgentClass). %0.0;.81% {28: 2à;4x}
               $0.0;.30;.46$ (Bacteria<->Protozoa)? {28: 2à;;}
               $0.0;.31;.30$ (CivicStructure-->Museum)? {28: Ib;;}
               $0.0;.31;.30$ (Museum-->CivicStructure)? {28: Ib;;}
               $0.0;.14;.24$ ((Pharmacy-->$1)==>(--,((VeterinaryCare~DiagnosticLab)-->$1)))? {29: 7F;aç;Fc;;}
               $0.0;.34;.22$ (Physician-->InfectiousAgentClass)? {29: Dr;;}
               $0.0;.34;.22$ (InfectiousAgentClass<->Physician)? {29: Dr;;}
               $0.0;.19;.27$ (MedicalOrganization-->Bacteria)? {29: 7F;;}
               $0.0;.19;.27$ (Bacteria-->MedicalOrganization)? {29: 7F;;}
               $0.0;.19;.27$ (Bacteria<->MedicalOrganization)? {29: 7F;;}
               $0.0;.23;.32$ (DiagnosticLab-->(Pharmacy|(VeterinaryCare~DiagnosticLab)))? {29: 7F;;}
               $0.0;.23;.32$ (DiagnosticLab<->(Pharmacy|(VeterinaryCare~DiagnosticLab)))? {29: 7F;;}
               $0.0;.28;.29$ (VeterinaryCare-->CivicStructure). %1.0;.40% {29: aç;lS;rò}
               $0.0;.29;.38$ (VeterinaryCare-->CivicStructure)? {29: lS;;}
               $0.0;.17;.19$ ((Hospital-->$1)==>(MedicalClinic-->$1))? {29: 7F;aç;rò;;}
               $0.0;.11;.16$ ((Zoo-->$1)==>(MedicalClinic-->$1))? {29: gh;lS;;}
               $0.0;.11;.16$ ((MedicalClinic-->$1)==>(Zoo-->$1))? {29: gh;lS;;}
               $0.0;.11;.16$ ((MedicalClinic-->$1)<=>(Zoo-->$1))? {29: gh;lS;;}
               $0.0;.24;.25$ ((Bacteria&Fungus)-->(Bacteria|Fungus))? {30: 4x;bl;;}
               $0.0;.24;.25$ ((Bacteria&Fungus)<->(Bacteria|Fungus))? {30: 4x;bl;;}
               $0.0;.36;.20$ ((#1-->MedicalBusiness)&&(((DiagnosticLab|VeterinaryCare)-->$2)==>(#1-->$2))). %1.0;.38% {30: 4É;7F;98;aç}
               $0.0;.36;.14$ ((($1-->MedicalBusiness)&&((DiagnosticLab|VeterinaryCare)-->$2))==>($1-->$2)). %1.0;.27% {30: 4É;7F;98;aç}
               $0.0;.47;.24$ (((availableService,MedicalClinic)-->$1)==>((medicalSpecialty,MedicalClinic)-->$1)). %1.0;.45% {30: B;3X}
               $0.0;.47;.24$ (((availableService,MedicalClinic)-->$1)<=>((medicalSpecialty,MedicalClinic)-->$1)). %1.0;.45% {30: B;3X}
               $0.0;.47;.24$ (((medicalSpecialty,MedicalClinic)-->$1)==>((availableService,MedicalClinic)-->$1)). %1.0;.45% {30: B;3X}
               $0.0;.47;.43$ (((availableService,MedicalClinic)-->#1)&&((medicalSpecialty,MedicalClinic)-->#1)). %1.0;.81% {30: B;3X}
               $0.0;.53;.29$ ((availableService,MedicalClinic)-->(medicalSpecialty,MedicalClinic))? {30: B;;}
               $0.0;.53;.26$ ((availableService,MedicalClinic)-->(medicalSpecialty,MedicalClinic)). %1.0;.45% {30: B;3X}
               $0.0;.53;.26$ ((availableService,MedicalClinic)<->(medicalSpecialty,MedicalClinic)). %1.0;.45% {30: B;3X}
               $0.0;.47;.43$ (((availableService,MedicalClinic)|(medicalSpecialty,MedicalClinic))-->domainIncludes). %1.0;.81% {30: B;3X}
               $0.0;.47;.43$ (((availableService,MedicalClinic)&(medicalSpecialty,MedicalClinic))-->domainIncludes). %1.0;.81% {30: B;3X}
               $0.0;.47;.43$ (((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->domainIncludes). %0.0;.81% {30: B;3X}
               $0.0;.53;.29$ ((availableService,MedicalClinic)<->(medicalSpecialty,MedicalClinic))? {30: B;;}
               $0.0;.19;.22$ ((Hospital-->$1)==>(Zoo-->$1))? {30: gh;lS;;}
               $0.0;.34;.57$ (Playground-->#1). %1.0;.73% {30: lá;Ib;;}
               $0.0;.31;.21$ (((epidemiology,PhysicalActivity)-->$1)==>(--,(((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->$1))). %1.0;.42% {31: B;1Ð;3X}
               $0.0;.31;.21$ ((--,(((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->$1))==>((epidemiology,PhysicalActivity)-->$1)). %1.0;.42% {31: B;1Ð;3X}
               $0.0;.31;.21$ ((--,(((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->$1))<=>((epidemiology,PhysicalActivity)-->$1)). %1.0;.42% {31: B;1Ð;3X}
               $0.0;.32;.38$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->#1)&&((epidemiology,PhysicalActivity)-->#1)). %0.0;.73% {31: B;1Ð;3X}
               $0.0;.35;.32$ (((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->(epidemiology,PhysicalActivity))? {31: B;3X;;}
               $0.0;.35;.24$ (((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))<->(epidemiology,PhysicalActivity)). %0.0;.42% {31: B;1Ð;3X}
               $0.0;.32;.38$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))|(epidemiology,PhysicalActivity))-->domainIncludes). %0.0;.73% {31: B;1Ð;3X}
               $0.0;.32;.38$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))&(epidemiology,PhysicalActivity))-->domainIncludes). %1.0;.73% {31: B;1Ð;3X}
               $0.0;.32;.38$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))~(epidemiology,PhysicalActivity))-->domainIncludes). %0.0;.73% {31: B;1Ð;3X}
               $0.0;.35;.32$ (((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))<->(epidemiology,PhysicalActivity))? {31: B;3X;;}
               $0.0;.24;.21$ (((seriousAdverseOutcome,MedicalDevice)-->$1)==>(--,(((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->$1))). %1.0;.42% {31: B;Ç;3X}
               $0.0;.24;.21$ ((--,(((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->$1))==>((seriousAdverseOutcome,MedicalDevice)-->$1)). %1.0;.42% {31: B;Ç;3X}
               $0.0;.24;.21$ ((--,(((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->$1))<=>((seriousAdverseOutcome,MedicalDevice)-->$1)). %1.0;.42% {31: B;Ç;3X}
               $0.0;.25;.38$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->#1)&&((seriousAdverseOutcome,MedicalDevice)-->#1)). %0.0;.73% {31: B;Ç;3X}
               $0.0;.27;.32$ (((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))-->(seriousAdverseOutcome,MedicalDevice))? {31: B;3X;;}
               $0.0;.27;.24$ (((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))<->(seriousAdverseOutcome,MedicalDevice)). %0.0;.42% {31: B;Ç;3X}
               $0.0;.25;.38$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))|(seriousAdverseOutcome,MedicalDevice))-->domainIncludes). %0.0;.73% {31: B;Ç;3X}
               $0.0;.25;.38$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))&(seriousAdverseOutcome,MedicalDevice))-->domainIncludes). %1.0;.73% {31: B;Ç;3X}
               $0.0;.25;.38$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))~(seriousAdverseOutcome,MedicalDevice))-->domainIncludes). %0.0;.73% {31: B;Ç;3X}
               $0.0;.27;.32$ (((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))<->(seriousAdverseOutcome,MedicalDevice))? {31: B;3X;;}
               $0.0;.19;.19$ (((epidemiology,PhysicalActivity)-->$1)<=>((seriousAdverseOutcome,MedicalDevice)-->$1))? {31: B;1Ð;3X;;}
               $0.0;.49;.25$ ((Emergency-->$1)==>(Dermatology-->$1)). %1.0;.45% {31: 4N;cÝ}
               $0.0;.49;.25$ ((Dermatology-->$1)<=>(Emergency-->$1)). %1.0;.45% {31: 4N;cÝ}
               $0.0;.49;.25$ ((Dermatology-->$1)==>(Emergency-->$1)). %1.0;.45% {31: 4N;cÝ}
               $0.0;.49;.44$ ((Dermatology-->#1)&&(Emergency-->#1)). %1.0;.81% {31: 4N;cÝ}
               $0.0;.60;.33$ (Emergency-->Dermatology)? {31: 4N;;}
               $0.0;.60;.30$ (Emergency-->Dermatology). %1.0;.45% {31: 4N;cÝ}
               $0.0;.60;.30$ (Dermatology<->Emergency). %1.0;.45% {31: 4N;cÝ}
               $0.0;.49;.44$ ((Dermatology|Emergency)-->MedicalBusiness). %1.0;.81% {31: 4N;cÝ}
               $0.0;.49;.44$ ((Dermatology&Emergency)-->MedicalBusiness). %1.0;.81% {31: 4N;cÝ}
               $0.0;.49;.44$ ((Emergency~Dermatology)-->MedicalBusiness). %0.0;.81% {31: 4N;cÝ}
               $0.0;.60;.33$ (Dermatology<->Emergency)? {31: 4N;;}
               $0.0;.49;.25$ ((Nursing-->$1)==>(Dermatology-->$1)). %1.0;.45% {31: 1H;cÝ}
               $0.0;.49;.25$ ((Dermatology-->$1)<=>(Nursing-->$1)). %1.0;.45% {31: 1H;cÝ}
               $0.0;.49;.25$ ((Dermatology-->$1)==>(Nursing-->$1)). %1.0;.45% {31: 1H;cÝ}
               $0.0;.49;.44$ ((Dermatology-->#1)&&(Nursing-->#1)). %1.0;.81% {31: 1H;cÝ}
               $0.0;.60;.33$ (Nursing-->Dermatology)? {31: 1H;;}
               $0.0;.60;.30$ (Nursing-->Dermatology). %1.0;.45% {31: 1H;cÝ}
               $0.0;.60;.30$ (Dermatology<->Nursing). %1.0;.45% {31: 1H;cÝ}
               $0.0;.49;.44$ ((Dermatology|Nursing)-->MedicalBusiness). %1.0;.81% {31: 1H;cÝ}
               $0.0;.49;.44$ ((Dermatology&Nursing)-->MedicalBusiness). %1.0;.81% {31: 1H;cÝ}
               $0.0;.49;.44$ ((Nursing~Dermatology)-->MedicalBusiness). %0.0;.81% {31: 1H;cÝ}
               $0.0;.60;.33$ (Dermatology<->Nursing)? {31: 1H;;}
               $0.0;.35;.24$ (((medicalSpecialty,MedicalClinic)-->$1)==>((medicalSpecialty,Hospital)-->$1)). %1.0;.45% {31: 3X;3ë}
               $0.0;.35;.24$ (((medicalSpecialty,Hospital)-->$1)<=>((medicalSpecialty,MedicalClinic)-->$1)). %1.0;.45% {31: 3X;3ë}
               $0.0;.35;.24$ (((medicalSpecialty,Hospital)-->$1)==>((medicalSpecialty,MedicalClinic)-->$1)). %1.0;.45% {31: 3X;3ë}
               $0.0;.35;.43$ (((medicalSpecialty,Hospital)-->#1)&&((medicalSpecialty,MedicalClinic)-->#1)). %1.0;.81% {31: 3X;3ë}
               $0.0;.39;.36$ ((medicalSpecialty,MedicalClinic)-->(medicalSpecialty,Hospital))? {31: 3X;;}
               $0.0;.39;.26$ ((medicalSpecialty,MedicalClinic)-->(medicalSpecialty,Hospital)). %1.0;.45% {31: 3X;3ë}
               $0.0;.39;.26$ ((medicalSpecialty,Hospital)<->(medicalSpecialty,MedicalClinic)). %1.0;.45% {31: 3X;3ë}
               $0.0;.35;.43$ (((medicalSpecialty,Hospital)|(medicalSpecialty,MedicalClinic))-->domainIncludes). %1.0;.81% {31: 3X;3ë}
               $0.0;.35;.43$ (((medicalSpecialty,Hospital)&(medicalSpecialty,MedicalClinic))-->domainIncludes). %1.0;.81% {31: 3X;3ë}
               $0.0;.35;.43$ (((medicalSpecialty,MedicalClinic)~(medicalSpecialty,Hospital))-->domainIncludes). %0.0;.81% {31: 3X;3ë}
               $0.0;.39;.36$ ((medicalSpecialty,Hospital)<->(medicalSpecialty,MedicalClinic))? {31: 3X;;}
               $0.0;.14;.24$ ((--,((Museum~Playground)-->$1))==>(TaxiStand-->$1))? {32: lá;t4;Ib;;}
               $0.0;.30;.26$ (Playground-->(Museum|Playground))? {32: Ib;;}
               $0.0;.30;.26$ (Playground<->(Museum|Playground))? {32: Ib;;}
               $0.0;.17;.23$ (availableService-->(/,(medicalSpecialty,MedicalClinic),_,MedicalClinic)). %1.0;.45% {32: B;3X;;}
               $0.0;.22;.24$ (domainIncludes-->(epidemiology,PhysicalActivity))? {32: B;3X;;}
               $0.0;.22;.24$ ((epidemiology,PhysicalActivity)-->domainIncludes)? {32: B;3X;;}
               $0.0;.22;.24$ (domainIncludes<->(epidemiology,PhysicalActivity))? {32: B;3X;;}
               $0.0;.22;.22$ (((availableService,MedicalClinic)&(medicalSpecialty,MedicalClinic))-->(((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))&(epidemiology,PhysicalActivity)))? {32: B;3X;;}
               $0.0;.22;.22$ ((((availableService,MedicalClinic)~(medicalSpecialty,MedicalClinic))&(epidemiology,PhysicalActivity))<->((availableService,MedicalClinic)&(medicalSpecialty,MedicalClinic)))? {32: B;3X;;}
               $0.0;.55;.31$ ((medicalSpecialty,MedicalClinic)-->#1). %1.0;.40% {33: B;3X;3ë}
               $0.0;.55;.31$ ((availableService,MedicalClinic)-->#1). %1.0;.40% {33: B;3X;3ë}
               $0.0;.17;.18$ ((TaxiStand-->$1)==>(--,((Museum~Playground)-->$1)))? {33: lá;t4;Ib;;}
               $0.0;.16;.28$ (((availableService,MedicalClinic)-->$1)==>((medicalSpecialty,Hospital)-->$1))? {33: 3X;3ë;;}
               $0.0;.16;.28$ (((medicalSpecialty,Hospital)-->$1)==>((availableService,MedicalClinic)-->$1))? {33: 3X;3ë;;}
               $0.0;.16;.28$ (((availableService,MedicalClinic)-->$1)<=>((medicalSpecialty,Hospital)-->$1))? {33: 3X;3ë;;}
               $0.0;.28;.46$ (Hospital-->MedicalOrganization). %1.0;.73% {33: aç;rò;;}
               $0.0;.28;.46$ (VeterinaryCare-->MedicalOrganization). %1.0;.73% {33: aç;rò;;}
               $0.0;.10;.13$ ((Museum-->$1)==>((Zoo~Hospital)-->$1))? {33: lá;Ib;;}
Disconnected from the target VM, address: '127.0.0.1:41852', transport: 'socket'

Process finished with exit code 0
