<template>
  <v-col>
    <v-hover v-slot="{ hover }">
      <v-sheet
        :color="getHoverEffect(hover)"
        class="d-flex flex-column align-center justify-center"
        :elevation="hover ? 12 : 0"
        rounded
      >
        <v-img
          class="rounded"
          max-width="200"
          max-height="300"
          :src="bookImageLink"
        />
        <div class="flex-wrap text-body-2 mt-1">
          {{ getUpdatedTitle(bookTitle) }}
        </div>
      </v-sheet>
    </v-hover>
  </v-col>
</template>

<script>
import {titleCase} from "title-case";

export default {
  name: "BookItem",
  props: {
    bookImageLink: {
      type: String,
      required: true,
      default: "Unavailable",
    },
    bookTitle: {
      type: String,
      required: true,
      default: "Unavailable",
    }
  },
  methods: {
    getUpdatedTitle(title) {
      return titleCase(title.toLowerCase());
    },
    getHoverEffect(hover) {
      return hover ? "blue-grey lighten-4" : "transparent";
    },
  }
}
</script>