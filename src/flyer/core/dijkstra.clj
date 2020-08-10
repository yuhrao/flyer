(ns flyer.core.dijkstra)

(defn- take-minnode [rdists preds]
  "Return a vector [minnode dist rdists preds]"
  (let [ [dist minnodes] (first rdists)
        [minnode pred] (first minnodes)
        others (rest minnodes)]
    [minnode
     dist
     (if (empty? others)
       (dissoc rdists dist)
       (assoc rdists dist others))
     (assoc preds minnode [pred dist])]))

(defn- get-children [net node]
  (keys (net node)))

(defn- get-distance [net nodesrc nodedst]
  ((net nodesrc) nodedst))

(defn- add-rdist 
  ([rdists node pred dist]
   "Add a known rdist (rdist = distance to the root)"
   (if-let [nodes (rdists dist)]
     (assoc rdists dist (assoc nodes node pred))
     (assoc rdists dist {node pred})))
  
  ([rdists node pred dist prevdist]
   (let [nrdists (add-rdist rdists node pred dist)
         minnodes (rdists prevdist)
         nminnodes (dissoc minnodes node)]
     (if (empty? nminnodes)
       (dissoc nrdists prevdist)
       (assoc nrdists prevdist nminnodes)))))

(defn- update-rdists [rdists preds net node dist]
  "Return [rdists preds] updated"
  (reduce (fn [acc x]
            (let [curdist (+ dist (get-distance net node x))
                  prevdist (second (preds x))
                  nrdists (first acc)
                  npreds (second acc)]
              (if (nil? prevdist)
                [(add-rdist nrdists x node curdist) (assoc npreds x [node curdist])]
                (if (< curdist prevdist)
                  [(add-rdist nrdists x node curdist prevdist) 
                   (assoc npreds x [node curdist])]
                  [nrdists npreds]))))
          [rdists preds]
          (get-children net node)))

(defn dijkstra [net root nodedst]
  (loop [rdists (sorted-map 0 {root root})
         minnode root
         preds {root [root 0]}
         dist 0]
    (if (empty? rdists)
      preds
      (let [[nminnode ndist nrdists npreds] (take-minnode rdists preds)
            [nnrdists nnpreds] (update-rdists nrdists 
                                              npreds 
                                              net 
                                              nminnode 
                                              ndist)]
        (recur nnrdists nminnode nnpreds ndist)))))
