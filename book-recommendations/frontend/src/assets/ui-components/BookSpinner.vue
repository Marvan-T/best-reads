<template>
  <div class="book-loader-container">
    <div class="loader book">
      <figure class="page"/>
      <figure class="page"/>
      <figure class="page"/>
    </div>
    <h1>Discovering Similar Reads</h1>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

h1 {
  color: #4A6D5A;
  text-align: center;
  font-family: sans-serif;
  font-size: 20px;
  position: relative;
}

h1:after {
  position: absolute;
  content: "";
  -webkit-animation: Dots 2s cubic-bezier(0, .39, 1, .68) infinite;
  animation: Dots 2s cubic-bezier(0, .39, 1, .68) infinite;
}

.loader {
  margin: 5% auto 30px;
}

.book {
  border: 4px solid #5D8263;
  width: 60px;
  height: 45px;
  position: relative;
  perspective: 150px;
}

.page {
  display: block;
  width: 30px;
  height: 45px;
  border: 4px solid #5D8263; /* Primary color for border */
  border-left: 1px solid #AD5D5D; /* Secondary color for page separator */
  margin: 0;
  position: absolute;
  right: -4px;
  top: -4px;
  overflow: hidden;
  background: #AD5D5D; /* Secondary color for page background */
  transform-style: preserve-3d;
  -webkit-transform-origin: left center;
  transform-origin: left center;
}

.book .page:nth-child(1) {
  -webkit-animation: pageTurn 1.2s cubic-bezier(0, .39, 1, .68) 1.6s infinite;
  animation: pageTurn 1.2s cubic-bezier(0, .39, 1, .68) 1.6s infinite;
}

.book .page:nth-child(2) {
  -webkit-animation: pageTurn 1.2s cubic-bezier(0, .39, 1, .68) 1.45s infinite;
  animation: pageTurn 1.2s cubic-bezier(0, .39, 1, .68) 1.45s infinite;
}

.book .page:nth-child(3) {
  -webkit-animation: pageTurn 1.2s cubic-bezier(0, .39, 1, .68) 1.2s infinite;
  animation: pageTurn 1.2s cubic-bezier(0, .39, 1, .68) 1.2s infinite;
}


/* Page turn */

@-webkit-keyframes pageTurn {
  0% {
    -webkit-transform: rotateY(0deg);
    transform: rotateY(0deg);
  }
  20% {
    background: #5D8263; /* Slightly lighter or different shade of primary color */
  }
  40%, 100% {
    background: #5D8263; /* Primary color */
    -webkit-transform: rotateY(-180deg);
    transform: rotateY(-180deg);
  }
}

@keyframes pageTurn {
  0% {
    transform: rotateY(0deg);
  }
  20% {
    background: #5D8263; /* Slightly lighter or different shade of primary color */
  }
  40%, 100% {
    background: #5D8263; /* Primary color */
    transform: rotateY(-180deg);
  }
}

/* Dots */

@-webkit-keyframes Dots {
  0% {
    content: "";
  }
  33% {
    content: ".";
  }
  66% {
    content: "..";
  }
  100% {
    content: "...";
  }
}

@keyframes Dots {
  0% {
    content: "";
  }
  33% {
    content: ".";
  }
  66% {
    content: "..";
  }
  100% {
    content: "...";
  }
}
</style>
