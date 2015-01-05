(ns overtone-experiments.experiments
  (:require [overtone.studio.fx])
  (:use [overtone.live]
        [overtone.samples.piano :only [index-buffer]]
        [overtone.inst.sampled-piano]))

(odoc bus-monitor)

(odoc in-bus-mixer)

(bus-monitor )


(definst sampled-piano2
  [note 60 level 1 rate 1 loop? 0
   attack 0 decay 1 sustain 1 release 0.1 curve -4 gate 1]
  (let [buf (index:kr (:id index-buffer) note)
        env (env-gen (adsr attack decay sustain release level curve)
                     :gate gate
                     :action FREE)]
    (* env (scaled-play-buf 2 buf :rate rate :level level :loop loop? :action FREE))))

(sampled-piano (note :E4) :level 1.5 :decay 0.5 :sustain 2 :attack 0.01)w

(sampled-piano2 (note :E4))

(inst-fx! sampled-piano fx/fx-reverb)

(kill sampled-piano)



(defsynth first-piano [note 66]
  (out 0 (sampled-piano  (:note note))))

(definst pink-noisey []
     (* (env-gen (perc 0.1 1.8) :action FREE)
        (pink-noise)))

(pink-noisey)

(definst sawzall [freq 440]
  (* (env-gen (perc 0.1 0.8) :action FREE)
     (saw freq)))

(sawzall)

(definst triangular [freq 120]
   (* (env-gen (perc 0.1 4.8) :action FREE)
      (lf-tri freq)))

(triangular 320)


(mix:ar)

(odoc mix:ar)
