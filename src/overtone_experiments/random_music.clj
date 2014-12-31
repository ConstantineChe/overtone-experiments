(ns overtone-experiments.random-music
  (:require [overtone.studio.fx :as fx])
  (:use [overtone.live]
        [overtone.inst.sampled-piano]
        [overtone.inst.piano]
        [overtone.inst.synth :as s]
        [overtone.synth.stringed]
        ))

(def piece [:E4 :F#4 :B4 :C#5 :D5 :F#4 :E4 :C#5 :B4 :F#4 :D5 :C#5])

(def blues-scale [[5 0] [5 3]
                  [4 0] [4 1] [4 2]
                  [3 0] [3 2]
                  [2 0] [2 2] [2 3]
                  [1 0] [1 3]
                  [0 0] [0 3]])

(def effect fx/fx-reverb)

(effect)
(kill effect)
(fx/fx-echo 0 5 10 15)
(kill fx/fx-echo)
(fx/free)


(def g (guitar))

(ctl g :pre-amp 1.8 :amp 7.9)

(guitar-strum g :Em :down 0.70)

(guitar-pick g 0 0)

(blues-guitar (now) blues-scale 2000)

(defn blues-guitar
  [t scale range]
  (let
      [n (rand-nth scale)
        t-next (+ t (rand-int range))]
    (when n
      (at t
          (guitar-pick g (first n) (second n)))
      (apply-by t-next #'blues-guitar [t-next scale range]))))

(piano (note :C#4))

(sampled-piano (note :F#5))
(sampled-piano (note :G#5))
(sampled-piano (note :A6))
(sampled-piano (note :B6))

(stop)

(randomplay (now) piece 6000)

(defn randomplay [t notes range]
  (let [n (rand-nth notes)
        t-next (+ t (rand-int range))]
    (when n
      (at t
          (sampled-piano (note n)))
      (apply-by t-next #'randomplay [t-next notes range]))))
