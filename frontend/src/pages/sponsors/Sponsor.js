import styles from './sponsors.module.css'
import sponsorsData from "../../Sponsor.json";

import React from "react";

export default class Sponsor extends React.Component {
  render() {
    const { title, url, logo, rank } = this.props; // Destructure props

    let width = 200;
    if (rank === 1) width = 290;
    else if (rank === 2) width = 250;
    else if (rank === 3) width = 220;
    const imagesPath="/images/sponsors/";
    return (
      <>
        {url!=null ? (
          <button>
            <a href={url} target="_blank" rel="noopener noreferrer">
            <img
              src={imagesPath+logo}
              alt={`${title} logo`}
              style={{ width, cursor: "pointer" }}
            />
          </a>
          </button>
        ) : (
          <button className={styles.no_website}>
            <img
            src={imagesPath+logo}
            alt={`${title} logo`}
            style={{ width }}
          />
          </button>
        )}
      </>
    );
  }
}
