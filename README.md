# api-artisans : project created with archetype

## Updating Version
In your project's `build-prod` job of the CI/CD pipeline, you can update the version of your project. You have the flexibility to increment either the major or the hotfix version as needed. Follow these steps to make the necessary version changes:

 **Modify Version Increment:**  
  Locate the `build-prod` job within the file. , in the job's script section, update the version increment line from:

   ```shell
   minor_version=$((minor_version + 1))
   ```
   to 
   ```shell
   <major_or_patch>_version=$((<major_or_patch>_version + 1))
