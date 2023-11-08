<template>
  <v-container class="pa-2 ml-0 mr-0 mt-5">
    <h3 class="pb-2 pl-1">
      More about this edition
    </h3>
    <v-card class="pa-4">
      <v-row>
        <v-col v-if="category" :cols="numberOfColumns">
          <p>Genre:</p>
        </v-col>
        <v-col :cols="numberOfColumns">
          <p v-if="category">
            {{ categories }}
          </p>
        </v-col>
        <v-col v-if="publisher" :cols="numberOfColumns">
          <p>Publisher:</p>
        </v-col>
        <v-col :cols="numberOfColumns">
          <p v-if="publisher">
            {{ publisher }}
          </p>
        </v-col>
      </v-row>
      <v-row class="mt-0">
        <v-col v-if="publishedDate" :cols="numberOfColumns">
          <p>Published date:</p>
        </v-col>
        <v-col :cols="numberOfColumns">
          <p v-if="publishedDate">
            {{ formattedDate }}
          </p>
        </v-col>
        <v-col v-if="pages" :cols="numberOfColumns">
          <p>Number of pages:</p>
        </v-col>
        <v-col :cols="numberOfColumns">
          <p v-if="pages">
            {{ pages }}
          </p>
        </v-col>
      </v-row>
      <v-row>
        <p v-if="originalDescription" class="font-italic pa-4">
          {{ description }}
        </p>
        <v-layout justify-center>
          <v-card-actions v-if="shouldShowDescriptionToggle" class="mb-5">
            <v-btn v-if="isShortDescription" @click="toggleDescription">
              View more
            </v-btn>
            <v-btn v-else @click="toggleDescription">
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
    category: {type: String, required: true},
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
