import React from "react";
import styles from './jeugd.module.css'; // Assuming you want to keep the custom styles in a separate CSS file

const Jeugd = () => {
  return (
    <main className={styles.main}>
      {/* Logo Section */}
      <section style={{ backgroundColor: "black" }}>
        <img  className={styles.logoJeugd} src="/images/logoJeugd.png" alt="logoJeugd" />
      </section>

      {/* Introduction Section */}
      <section style={{ backgroundColor: "gray" }}>
        <p>
          Het instaporkest van onze fanfare staat voor een inspirerende reis door de wereld van muziek. We verwelkomen enthousiaste jongeren en beginnende muzikanten om samen met ons te groeien en te leren.
        </p>
        <p>
          Ons doel is om een omgeving te creëren waarin muzikanten van alle niveaus zich kunnen ontwikkelen en hun passie voor muziek kunnen delen. Of je nu een ervaren muzikant bent of net begint, bij ons vind je een plek om je talent te ontplooien.
        </p>
        <p>
          Dus, als je zin hebt om deel uit te maken van een gezellige groep muzikanten en samen te genieten van de magie van muziek, dan ben je bij ons aan het juiste adres. Kom erbij en laten we samen een muzikaal avontuur beleven!
        </p>
      </section>

      {/* Music Lessons Section */}
      <section style={{ backgroundColor: "white", color: "black" }}>
        <p>
          Bij onze fanfare zetten we in op muzikale groei voor iedereen. We bieden gratis muzieklessen aan voor alle instrumenten die je in een fanfare kunt vinden.
        </p>
        <p>
          Of je nu droomt van het spelen van een trompet, saxofoon, slagwerk, of een ander instrument uit de fanfare, onze getalenteerde instructeurs staan klaar om je te begeleiden op jouw muzikale reis. De lessen zijn gratis, zodat iedereen de kans krijgt om zijn passie voor muziek te ontdekken en te ontwikkelen.
        </p>
        <p>
          Sinds enkele jaren werken we ook nauw samen met de basisschool “De Negensprong”.
        </p>
        <p>
          Dus, aarzel niet! Sluit je aan bij ons en laat de muzieklessen je inspireren terwijl je jezelf onderdompelt in de prachtige wereld van fanfaremuziek.
        </p>
      </section>

      {/* Youth Group Section */}
      <section>
        <p>
          De jeugdwerking van het instaporkest is een groep enthousiaste leden die zich inzet voor het welzijn en plezier van de jonge leden. Ze zijn verantwoordelijk voor het organiseren van diverse activiteiten om de band tussen de jongeren te versterken en hen buiten het musiceren om een geweldige tijd te bezorgen.
        </p>
        <p>
          Elk jaar organiseert de jeugdwerking een aantal uitstappen voor de leden. Deze uitstappen kunnen bijvoorbeeld bezoeken aan pretparken, musea, of andere leuke locaties zijn, waar de jongeren samen kunnen genieten van een dag vol avontuur en plezier.
        </p>
        <p>
          Naast de grote uitstappen worden er ook kleinere evenementen georganiseerd, zoals de kermis, het snackfestijn en het Sint-Ceciliafeest. Zo zorgen we voor een hechte band en een toffe sfeer in het Instaporkest!
        </p>
        <p>
          Hieronder stellen we graag onze jeugdwerking kort voor!
        </p>
      </section>

      {/* Youth Group Members Section */}
      <section style={{ backgroundColor: "gray" }}>
        <p>
          Pascale, onze bariton saxofonist, is niet alleen een muzikant, maar ook een drijvende kracht in ons bestuur. Ze organiseert muzieklessen en deelt haar passie voor muziek met anderen.
        </p>
        <p>
          Seppe is op pad om architect te worden. Daarnaast beoefent hij het slagwerk alsook speelt hij recentelijk op de bastuba.
        </p>
        <p>
          Lance behaalde net zijn master in de economie. Tevens vormt hij met Seppe een dynamisch slagwerk duo in het orkest.
        </p>
        <p>
          Ruben is de begaafde bugelspeler en ingenieur van de groep.
        </p>
        <p>
          Bjarne is de jongste van de bende en is op weg om mecanicien te worden. Hij speelt niet alleen graag met auto’s maar ook op zijn trouwe saxofoon.
        </p>
        <p>
          Robbe volgt een richting in informatica. Hierdoor is hij uiterst geschikt voor alle IT-gerelateerde zaken. Naast zijn passie voor IT, bespeelt Robbe de trompet.
        </p>
      </section>

      {/* Membership Section */}
      <section style={{ backgroundColor: "white", color: "black" }}>
        <p>
          Wil je meespelen in ons instaporkest bij de fanfare? Geweldig! We verwelkomen graag nieuwe leden, ongeacht je ervaringsniveau.
        </p>
        <p>
          Je kunt contact met ons opnemen via sociale media voor meer informatie over lidmaatschap en repetities. We kijken ernaar uit om samen muziek te maken! Daarnaast ben je van harte welkom om langs te komen op een van onze repetities, die plaatsvinden op vrijdag om 19 uur. We ontmoeten je graag en helpen je graag om deel uit te maken van ons muzikale avontuur.
        </p>
      </section>
    </main>
  );
};

export default Jeugd;
