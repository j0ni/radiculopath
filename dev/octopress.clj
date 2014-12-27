(ns octopress
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn make-map-pair [line]
  (let [pair (str/split line #":")
        k    (keyword (first pair))
        v    (-> pair
                 second
                 str/trim
                 (str/replace #"\"" ""))]
    {k v}))

(def lines (memoize
            (fn [text]
              (-> text str/split-lines rest))))

(defn parse-map [text]
  (let [map-pairs (->> text
                       lines
                       (take-while #(not= "---" %))
                       (map make-map-pair))]

    (as-> map-pairs r
          (into {} r)
          (select-keys r [:title :layout]))))

(defn parse-text [text]
  (let [text (->> text lines (drop-while #(not= "---" %)))]
    (rest text)))

(defn file-list [dir]
  (file-seq (io/file dir)))

(defn process-post [text tags]
  (let [meta-map (-> text parse-map (merge tags) pr-str)
        content (parse-text text)
        join #(str/join "\n" %)]
    (-> content (conj meta-map) join)))

(defn migrate-octopress
  ([src-dir dst-dir tags]
   (let [files (-> src-dir file-list rest)]
     (doseq [in-file files]
       (let [out-filename (-> (.getName in-file)
                              (str/split #"\.")
                              first
                              (str ".md"))
             out-file (io/file dst-dir out-filename)
             res (-> in-file slurp (process-post {:tags tags}))]
         (spit out-file res)))))
  ([src-dir dst-dir]
   (migrate-octopress src-dir dst-dir [])))
