(ns overtone-experiments.drums
  (:require [overtone.studio.fx :as fx])
  (:use [overtone.live]
        [overtone.inst.sampled-piano]
        [overtone.samples.piano :only [index-buffer]]
        [overtone.inst.synth]
        [overtone.inst.drum :as d]))


(def drums-state (atom {
                        :snare {:amp 5 :crackle-amp 20}
                        :hat {:amp 2 :t 0.7 :low 6000 :hi 2000}
                        :soft-hat {:decay 0.5}
                        }))

(swap! drums-state update-in [:snare :tightness] (fn [x] 1000))
(swap! drums-state update-in [:hat :t] (fn [x] 0.7))
(swap! drums-state update-in [:soft-hat :decay] (fn [x] 0.2))

(fx/fx-reverb)
(kill fx/fx-reverb)

(inst-fx! d/snare fx/fx-reverb)

(fx/fx-echo)

(ctl (fx/fx-echo) :delay-time 1.7 :decay-time 0.5 :max-delay 0.1)

(ctl fx/fx-echo :decay-time 2.1)

(kill fx/fx-echo)

(def metro (metronome 70))

(defn metro-fx [m fx instr step clr]
  (let [beat (m)]
    (at (m beat) (if (zero? (mod beat step)) (inst-fx! instr fx)))
    (at (m (+ clr beat)) (if (zero? (mod beat clr)) (clear-fx instr)))
    (apply-by (m (inc beat)) metro-fx m fx instr step clr [])))

(metro :bpm 90)

(defn play-beat [m sound step ctl-ref ctl-atom]
  (let [beat (m)]
    (at (m beat) (if (zero? (mod beat step)) (apply sound (ctl-deref ctl-ref ctl-atom))))
    (apply-by (m (inc beat)) play-beat m sound step ctl-ref ctl-atom [])))


(defn ctl-deref [ctl-ref ctl-atom]
  (let [ctl-map (ctl-ref @ctl-atom)]
    (apply concat (for [key (keys ctl-map)]
                    (list key (get ctl-map key))))))

(ctl-deref :hat drums-state)

(play-beat metro d/snare 1 :snare drums-state)
(play-beat metro d/soft-hat 2 :hat drums-state)
(play-beat metro d/soft-hat 4 :soft-hat drums-state)
(metro-fx metro fx/fx-reverb d/snare 3 1)
(metro-fx metro fx/fx-echo d/soft-hat 8 4)

(ctl d/snare :crackle-amp 20 :amp 5)
(d/soft-hat )

(metro)

(stop)

(inst-fx! d/snare fx/fx-echo)
(clear-fx d/snare)

(print (ctl d/snare :crackle-amp 20.0 :amp 5))
(def my-hat (d/open-hat :amp 2 :t 0.7))
(def my-snare (d/snare :crackle-amp 20.0 :amp 5))
(d/closed-hat)
