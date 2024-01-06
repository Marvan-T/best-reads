[![CI workflow](https://github.com/Marvan-T/best-reads/actions/workflows/ci.yml/badge.svg)](https://github.com/Marvan-T/best-reads/actions/workflows/ci.yml)

# Best Reads

"Best Reads" is a web application that was originally developed as a final year group project at
university. Our aim is to enhance the book discovery experience for users. This repository is a fork
of the original project, which I maintain, and includes an extension layer that adds new features to
the project.

- Original Project
  Repository: [Best Reads](https://github.com/laurenmaylittle-cs/book-recommendations)
- Extension Project: [Best Reads Extesions](https://github.com/Marvan-T/bestreads-extensions)

## Deployment

The project is deployed on Azure. You can visit the application using either of the following URLs:

- Main URL: [Best Reads](https://best-reads.live)
- Alternative URL: [Best Reads on Azure](https://best-reads.azurewebsites.net)

## Features currently implemented

- **Weekly Best Sellers**: Showcases the weekly best sellers from the New York Times.
- **Book Descriptions**: Allows users to learn more about different books through their
  descriptions.
- **Search Functionality**: Enables users to search for books by title, authors, and ISBN.
- **Authentication**: Implemented using Auth0.
- **Collections**: Provides the ability to organize books into collections.
- **Recommendations**: Offers similar items recommendations. This feature was originally implemented
  using AWS Personalise and later re-implemented in the extension project using embeddings.
- **Ratings**: Allows users to rate books.

## What's Coming Up Next?

I've got some exciting updates in the works to make your experience with books discovery better.
Also,
I'm planning to migrate the client from Vue 2 to Vue 3.
