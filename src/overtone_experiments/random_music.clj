(ns overtone-experiments.random-music
  (:require [overtone.studio.fx :as fx])
  (:use [overtone.live]
        [overtone.inst.sampled-piano]
        [overtone.inst.piano]
        [overtone.inst.synth :as s]
        [overtone.synth.stringed]
        ))

(def piece [:E4 :F#4 :B4 :C#5 :D5 :F#4 :E4 :C#5 :B4 :F#4 :D5 :C#5])

(def E-minor [:E5 :F#5 :G5 :A6 :B6 :C6 :D6 :E6])

(def blues-scale [[5 3] [5 6]
                  [4 3] [4 4] [4 5]
                  [3 3] [3 5]
                  [2 3] [2 5] [2 6]
                  [1 3] [1 6]
                  [0 3] [0 6]])

(def effect fx/fx-reverb)

(effect)
(kill effect)
(fx/fx-echo 0 5 10 15)
(kill fx/fx-echo)
(fx/free)

(clear-fx sampled-piano)

(def g (guitar))
(def g2 (guitar))

(ctl g2 :pre-amp 2.8 :amp 3.9
     :rvb-mix 1.75 :rvb-room 1.4
     :distortion 6.5)

(ctl g :pre-amp 2.8 :amp 8.2
     :rvb-mix 0.1 :rvb-room 0.1)

(inst-fx! sampled-piano fx/fx-reverb)
(inst-fx! g fx/fx-reverb)


(guitar-strum g :Em :down 0.70)
(guitar-strum g2 :Em :down 1)

(guitar-pick g2 0 0)

(def range1 (atom [1000 1000]))
(def range2 (atom [1000 1000]))

(swap! range1 (fn [x] (vector 100 1000)))
(swap! range2 (fn [x] (vector 100 100)))

(random-guitar (now) blues-scale range1 g)
(random-guitar (now) blues-scale range2 g2)

(defn random-guitar
  [t scale range gtr]
  (let
      [n (rand-nth scale)
       t-next (+ t (first @range) (rand-int (second @range)))]
    (when n
      (at t
          (guitar-pick gtr (first n) (second n)))
      (apply-by t-next #'random-guitar [t-next scale range gtr]))))

(piano (note :C#4))

(sampled-piano (note :F#5))
(sampled-piano (note :G#5))
(sampled-piano (note :A6))
(sampled-piano (note :B6))

(stop)

(randomplay (now) piece 6000)
(randomplay (now) E-minor 3000)

(defn randomplay [t notes range]
  (let [n (rand-nth notes)
        t-next (+ t (rand-int range))]
    (when n
      (at t
          (sampled-piano (note n)))
      (apply-by t-next #'randomplay [t-next notes range]))))
