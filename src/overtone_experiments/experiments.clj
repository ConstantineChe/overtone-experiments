(ns overtone-experiments.experiments
  (:require [overtone.studio.fx])
  (:use [overtone.live]
        [overtone.inst.sampled-piano]))

(odoc bus-monitor)

(odoc in-bus-mixer)

(bus-monitor )



(defsynth first-piano [note 66]
  (out 0 (sampled-piano :note note)))

(mix:ar)

(odoc mix:ar)
