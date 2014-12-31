(ns overtone-experiments.experiments
  (:use [overtone.live]))

(odoc bus-monitor)

(odoc in-bus-mixer)

(bus-monitor )



(defsynth first-piano [note 66]
  (out 0 (sampled-piano :note note)))

(mix:ar)

(odoc mix:ar)
