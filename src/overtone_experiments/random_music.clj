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
(fx/free-verb)
(fx/fx-sustainer)

(clear-fx sampled-piano)

(def g (guitar))
(def g2 (guitar))

(ctl g2 :pre-amp 3.2 :amp 1.9
     :rvb-mix 1.0 :rvb-room 1
     :distort 0.6 :dur 50 :pan 0
     :noise-amp 0.9 :rvb-damp 0.0
     :decay 10)

(ctl g :pre-amp 3.8 :amp 8.2
     :rvb-mix 0.4 :rvb-room 0.4
     :rvb-damp 0.9 :lp-rq 0.25)



(inst-fx! sampled-piano fx/fx-reverb)
(inst-fx! guitar fx/fx-reverb)
(kill fx/fx-freeverb)

(fx/fx-freeverb 0 0.9 0.8 0.7)

(fx/fx-chorus 0 0.2 0.01)
(kill fx/fx-chorus)

(guitar-strum g :Em :down 0.70)
(guitar-strum g2 :Em :down 1)

(guitar-pick g 5 5)

(def range1 (atom [1000 1000]))
(def range2 (atom [1000 1000]))

(swap! range1 (fn [x] (vector 1000 1000)))
(swap! range2 (fn [x] (vector 1000 1000)))

(inst? sampled-piano)

(random-guitar (now) blues-scale range1 g true)
(random-guitar (now) blues-scale range2 g2 false)

(stop)

(defn random-guitar
  [t scale range gtr silent]
  (let
      [n (rand-nth scale)
       t-next (+ t (first @range) (rand-int (second @range)))]
    (when n
      (at t
          (if silent (guitar-strum gtr [-1 -1 -1 -1 -1 -1]))
          (guitar-pick gtr (first n) (second n)))
      (apply-by t-next #'random-guitar [t-next scale range gtr silent]))))

(piano (note :C#4))

(def piano1 (sampled-piano))
(def piano2 (sampled-piano))

(piano1 (note :E5))

(sampled-piano (note :F#5))
(sampled-piano (note :G#5))
(sampled-piano (note :A6))
(sampled-piano (note :B6))

(stop)

(randomplay (now) piece 60000)
(randomplay (now) E-minor 3000)

(defn randomplay [t notes range]
  (let [n (rand-nth notes)
        t-next (+ t (rand-int range))]
    (when n
      (at t
          (sampled-piano (note n)))
      (apply-by t-next #'randomplay [t-next notes range]))))

(do
   (defn piano-note [n]
     (sampled-piano (note n)))

   (defn strum [ c ]
     (doall (map piano-note (apply chord c))))

   (defn strumming [ m beat ]
     (let [gchords [ (choose '([:e3 :major] [:e3 :major7] [:e3 :minor]))
                     (choose '([:e3 :major] [:e3 :major7] [:e3 :minor]))
                     (choose '([:g3 :major] [:g3 :major7] [:g3 :minor]))
                     (choose '([:e3 :major] [:e3 :major7] [:e3 :minor]))
                     (choose '([:a3 :major] [:a3 :major7] [:a3 :minor]))
                     (choose '([:e3 :major] [:e3 :major7] [:e3 :minor]))
                     ]
           next-measure-beat (+ beat (* 2 (.size gchords)))
           ]
       (println "beat: " beat " " gchords)
       (doseq [[index cur-chord]
               (map vector (iterate inc 0) gchords)]
         (at (m (+ beat (* 2 index))) (strum cur-chord)))
       (apply-at (m next-measure-beat) #'strumming m next-measure-beat [])))
   )

(def metro (metronome 120))
(ctl metro )
(strumming metro (metro))
(stop)
