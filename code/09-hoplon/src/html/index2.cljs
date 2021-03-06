(ns tailrecursion.presioke.index2
  (:require-macros
   [tailrecursion.hoplon.macros  :refer [reactive-attributes]]
   [tailrecursion.javelin.macros :refer [cell]]
   [alandipert.interpol8         :refer [interpolating]])
  (:require
   tailrecursion.javelin
   [tailrecursion.hoplon.reactive :as d]
   [tailrecursion.presioke.flickr :refer [flickr-api-map image-url]]))

;;; flickr API

(defn flickr
  "Invokes the flickr API method with params and calls the success
  function."
  [method params success]
  (.ajax js/jQuery (flickr-api-map method params success)))

;;; cells

(def images
  "Stem cell. Vector of image URLs. Contains an initial default image."
  (cell '["http://www.auburn.edu/~burnsma/peopl96a.gif"]))

(def cursor
  "Input cell. Index of the current image URL."
  (cell 0))

(def ready?
  "Formula cell. True when images remain. Accounts for the default image."
  (cell (> (dec (count images)) cursor)))

(def current-image
  "Formula cell. URL of the currently selected image."
  (cell (get images cursor)))

;;; initialize

(.on (js/jQuery "body") "keypress click" #(swap! cursor inc))

(flickr "flickr.interestingness.getList"
        {"api_key" "d4fbe84122c1fb2c58dcdd974f5e46ef", "per_page" 500}
        #(swap! images into (map image-url (get-in % ["photos" "photo"]))))

(html
 (head (title "Presioke"))
 (body
  (reactive-attributes
   (img {:do [(d/attr! :src current-image)]})
   (br)
   (span {:do [(d/text! (if ready? (str (inc cursor) "/" (count images)) "Loading..."))]})
   (br)
   (span {:do [(d/text! (if ready? "Press a key or click the mouse for a new image."))]}))))
