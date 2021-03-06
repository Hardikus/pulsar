(def quasar-version "0.7.1-SNAPSHOT")
(defproject co.paralleluniverse/pulsar "0.7.1-SNAPSHOT"
  :description "A Clojure lightweight thread, asynchronous programming, and actor library"
  :url "http://github.com/puniverse/pulsar"
  :licenses [{:name "Eclipse Public License - v 1.0" :url "http://www.eclipse.org/legal/epl-v10.html"}
             {:name "GNU Lesser General Public License - v 3" :url "http://www.gnu.org/licenses/lgpl.html"}]
  :min-lein-version "2.5.0"
  :distribution :repo
  :source-paths      ["src/main/clojure"]
  :test-paths        ["src/test/clojure"]
  :resource-paths    ["src/main/resources"]
  :java-source-paths ["src/main/java"]
  :javac-options     ["-target" "1.7" "-source" "1.7"]
  :repositories {"snapshots" "https://oss.sonatype.org/content/repositories/snapshots"
                 "releases" "https://oss.sonatype.org/content/repositories/releases"}
  :test-selectors {:selected :selected}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [co.paralleluniverse/quasar-core   ~quasar-version] ; :classifier "jdk8"
                 [co.paralleluniverse/quasar-actors ~quasar-version]
                 [org.ow2.asm/asm "5.0.3"]
                 [org.clojure/core.match "0.2.2" :exclusions [org.ow2.asm/*]]
                 [useful "0.8.8"]
                 [gloss "0.2.5" :exclusions [com.yammer.metrics/metrics-core useful]]
                 [org.clojure/core.typed "0.2.92" :exclusions [org.apache.ant/ant org.clojure/core.unify org.ow2.asm/*]]]
  :manifest {"Premain-Class" "co.paralleluniverse.fibers.instrument.JavaAgent"
             "Can-Retransform-Classes" "true"}
  :jvm-opts ["-server"
             ;"-Dclojure.compiler.disable-locals-clearing=true"
             ;; ForkJoin wants these:
             "-XX:-UseBiasedLocking"
             "-XX:+UseCondCardMark"]
  ;:injections [(alter-var-root #'*compiler-options* (constantly {:disable-locals-clearing true}))]
  :java-agents [[co.paralleluniverse/quasar-core ~quasar-version :options "m"]] ; :classifier "jdk8" :options "vd"
  :pedantic :warn
  :profiles {;; ----------- dev --------------------------------------
             :dev
             {:plugins [[lein-midje "3.1.3"]]
              :dependencies [[midje "1.6.3" :exclusions [org.clojure/tools.namespace]]]
              :jvm-opts [;; Debugging
                         "-ea"
                         ;"-Dco.paralleluniverse.fibers.verifyInstrumentation=true"
                         ;"-Dco.paralleluniverse.fibers.detectRunawayFibers=false"
                         ;"-Dco.paralleluniverse.fibers.traceInterrupt=true"
                         ;; Recording
                         ;"-Dco.paralleluniverse.debugMode=true"
                         ;"-Dco.paralleluniverse.globalFlightRecorder=true"
                         ;"-Dco.paralleluniverse.monitoring.flightRecorderLevel=2"
                         ;"-Dco.paralleluniverse.flightRecorderDumpFile=pulsar.log"
                         ;"-Xdebug"
                         ;"-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
                         ;; Logging
                         "-Dlog4j.configurationFile=log4j.xml"
                         "-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
                         ]
              :global-vars {*warn-on-reflection* true}}

             ;; ----------- cluster --------------------------------------
             :cluster
             {:repositories {"oracle" "http://download.oracle.com/maven/"}
              :dependencies [[co.paralleluniverse/quasar-galaxy ~quasar-version]]
              :java-source-paths ["src/cluster/java"]
              :jvm-opts [;; Debugging
                         "-ea"
                         ;; Galaxy
                         "-Djgroups.bind_addr=127.0.0.1"
                         ; "-Dgalaxy.nodeId=1"
                         ; "-Dgalaxy.port=7051"
                         ; "-Dgalaxy.slave_port=8051"
                         "-Dgalaxy.multicast.address=225.0.0.1"
                         "-Dgalaxy.multicast.port=7050"
                         "-Dco.paralleluniverse.galaxy.configFile=src/test/clojure/co/paralleluniverse/pulsar/examples/cluster/config/peer.xml"
                         "-Dco.paralleluniverse.galaxy.autoGoOnline=true"
                         ;; Logging
                         "-Dlog4j.configurationFile=log4j.xml"
                         ;"-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
                         ]}

             ;; ----------- doc --------------------------------------
             :doc
             {:plugins [[lein-midje "3.1.3"]
                        [codox "0.8.12"]
                        [lein-marginalia "0.8.0"]]
              :dependencies [[midje "1.6.3"]]
              :exclusions [org.clojure/tools.namespace]
              :injections [(require 'clojure.test)
                           (alter-var-root #'clojure.test/*load-tests* (constantly false))]
              :codox {:include [co.paralleluniverse.pulsar.core
                                co.paralleluniverse.pulsar.rx
                                co.paralleluniverse.pulsar.actors
                                co.paralleluniverse.pulsar.lazyseq
                                co.paralleluniverse.pulsar.async]
                      :output-dir "docs/api"}
              :global-vars {*warn-on-reflection* false}}
             ;; ----------- other instrumentation strategies ---------
             :auto-instrument-all
             {:jvm-opts ["-Dco.paralleluniverse.pulsar.instrument.auto=all"]}})
