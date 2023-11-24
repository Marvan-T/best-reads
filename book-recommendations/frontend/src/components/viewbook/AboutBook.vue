<template>
  <v-container class="pa-2 ml-0 mr-0 mt-5">
    <v-card
      class="pa-4"
      color="#D6D6D6"
    >
      <v-row>
        <template v-if="category && category !== ''">
          <v-col :cols="numberOfColumns">
            <p class="font-weight-bold text--primary">
              Genre:
            </p>
          </v-col>
          <v-col :cols="numberOfColumns">
            <p class="font-weight-light text--primary">
              {{ categories }}
            </p>
          </v-col>
        </template>
        <v-col
          v-if="publisher"
          :cols="numberOfColumns"
        >
          <p class="font-weight-bold text--primary">
            Publisher:
          </p>
        </v-col>
        <v-col :cols="numberOfColumns">
          <p
            v-if="publisher"
            class="font-weight-light text--primary"
          >
            {{ publisher }}
          </p>
        </v-col>
      </v-row>
      <v-row class="mt-0">
        <v-col
          v-if="publishedDate"
          :cols="numberOfColumns"
        >
          <p class="font-weight-bold text--primary">
            Published date:
          </p>
        </v-col>
        <v-col :cols="numberOfColumns">
          <p
            v-if="publishedDate"
            class="font-weight-light text--primary"
          >
            {{ formattedDate }}
          </p>
        </v-col>
        <v-col
          v-if="pages"
          :cols="numberOfColumns"
        >
          <p class="font-weight-bold text--primary">
            Number of pages:
          </p>
        </v-col>
        <v-col :cols="numberOfColumns">
          <p
            v-if="pages"
            class="font-weight-light text--primary"
          >
            {{ pages }}
          </p>
        </v-col>
      </v-row>
      <v-row>
        <p
          v-if="originalDescription"
          class="font-weight-light text--primary pa-4"
        >
          {{ description }}
        </p>
        <v-layout justify-center>
          <v-card-actions
            v-if="shouldShowDescriptionToggle"
            class="mb-5"
          >
            <v-btn
              v-if="isShortDescription"
              outlined
              color="#5D8263"
              @click="toggleDescription"
            >
              View more
            </v-btn>
            <v-btn
              v-else
              outlined
              color="#AD5D5D"
              @click="toggleDescription"
            >
              View less
            </v-btn>
          </v-card-actions>
        </v-layout>
      </v-row>
    </v-card>
  </v-container>
</template>

<script>
export default {
  name: "AboutBook",
  props: {
    category: {type: String, required: false, default: ""},
    publishedDate: {type: String, required: true},
    originalDescription: {type: String, required: true},
    pages: {type: Number, required: true},
    publisher: {type: String, required: false, default: "N/A"}
  },
  data: () => ({
    isShortDescription: true
  }),
  computed: {
    numberOfColumns() {
      return this.$vuetify.breakpoint.xs ? 6 : 3;
    },
    formattedDate() {
      const date = new Date(this.publishedDate);
      const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
        "Nov", "Dec"];
      return `${date.getDate()} ${monthNames[date.getMonth()]} ${date.getFullYear()}`;
    },
    description() {
      return this.isShortDescription
        ? this.truncateText(this.originalDescription, 400)
        : this.originalDescription;
    },
    shouldShowDescriptionToggle() {
      return this.originalDescription && this.originalDescription.length > 400;
    },
    categories() {
      return this.category;
    }
  },
  methods: {
    toggleDescription() {
      this.isShortDescription = !this.isShortDescription;
    },
    truncateText(text, maxCharacterCount) {
      return text.length > maxCharacterCount ? text.substring(0, maxCharacterCount) + '...' : text;
    }
  }
}
</script>

<style scoped>
</style>
