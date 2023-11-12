import Vue from 'vue'
import Vuetify from 'vuetify/lib/framework'

Vue.use(Vuetify)

export default new Vuetify({
  theme: {
    themes: {
      light: {
        primary: '#5D8263', //earthy green primary color
        secondary: '#AD5D5D', //muted rose secondary color
      },
    },
  },
});
