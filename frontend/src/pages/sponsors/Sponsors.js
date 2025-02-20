import React, { useEffect, useState } from "react";
import sponsorsData from "../../Sponsors"; // Import sponsors from JS file
import Sponsor from "./Sponsor"; // Correct import
import styles from './sponsors.module.css';

export default function SponsorsComponent() {
  const [sponsors, setSponsors] = useState([]);

  useEffect(() => {
    // Shuffle function
    const shuffle = (array) => {
      for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
      }
    };

    // Order sponsors by rank
    const rank1 = sponsorsData.filter((sponsor) => sponsor.rank === 1);
    const rank2 = sponsorsData.filter((sponsor) => sponsor.rank === 2);
    const rank3 = sponsorsData.filter((sponsor) => sponsor.rank === 3);
    const rank4 = sponsorsData.filter((sponsor) => sponsor.rank === 4);

    shuffle(rank2);
    shuffle(rank3);
    shuffle(rank4);

    // Combine sponsors in order of priority
    const orderedSponsors = [...rank1, ...rank2, ...rank3, ...rank4];
    setSponsors(orderedSponsors);
  }, []);

  return (
    <main className={styles.main} style={{ display: "grid", gap: "1rem", justifyContent: "center" }}>
      {sponsors.map((sponsor, index) => (
        <section key={index} style={{ margin: "1rem" }}>
          <Sponsor {...sponsor} /> {/* Spread props */}
        </section>
      ))}
    </main>
  );
}
