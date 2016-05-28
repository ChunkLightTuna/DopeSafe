# How to contribute

Feel free to report any bugs under [issues](https://github.com/TeamSeabiscuit/DopeSafe/issues). 

If you'd like to contribute code, here's how you can help:

1. Fork it
2. improve it
3. submit a [pull request](https://help.github.com/articles/creating-a-pull-request)

Your work will then be reviewed as soon as possible (suggestions about some
changes, improvements or alternatives may be given).

## Help with Git

Once the repository is forked, you should track the upstream (original) one
using the following command:

    git remote add upstream https://github.com/TeamSeabiscuit/DopeSafe.git

Then you should create your own branch, following the
[branch naming policy](VERSIONING.md#branch-naming):

    git checkout -b <prefix>/<micro-title>-<issue-number>

Once your changes are done (`git commit -am '<descriptive-message>'`), get the
upstream changes:

    git checkout master
    git pull --rebase origin master
    git pull --rebase upstream master
    git checkout <your-branch>
    git rebase master

Finally, publish your changes:

    git push -f origin <your-branch>

You should be now ready to make a pull request.

## Commit messages

The cleaner the git history is, the better.
