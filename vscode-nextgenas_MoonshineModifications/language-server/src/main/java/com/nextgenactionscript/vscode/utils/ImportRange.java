/*
Copyright 2016-2017 Bowler Hat LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.nextgenactionscript.vscode.utils;

import java.nio.file.Paths;

import org.apache.royale.compiler.tree.as.IASNode;
import org.apache.royale.compiler.tree.as.IFileNode;
import org.apache.royale.compiler.tree.as.IPackageNode;

public class ImportRange
{
	public String uri = null;
	public int startIndex = -1;
	public int endIndex = -1;

    public static ImportRange fromOffsetNode(IASNode offsetNode)
    {
        ImportRange range = new ImportRange();
        if (offsetNode != null)
        {
            //if we have an offset node, try to find where imports may be added
            IPackageNode packageNode = (IPackageNode) offsetNode.getAncestorOfType(IPackageNode.class);
            if (packageNode == null)
            {
                IFileNode fileNode = (IFileNode) offsetNode.getAncestorOfType(IFileNode.class);
                if (fileNode != null)
                {
                    boolean foundPackage = false;
                    for (int i = 0; i < fileNode.getChildCount(); i++)
                    {
                        IASNode childNode = fileNode.getChild(i);
                        if (foundPackage)
                        {
                            //this is the node following the package
                            range.startIndex = childNode.getAbsoluteStart();
                            break;
                        }
                        if (childNode instanceof IPackageNode)
                        {
                            //use the start of the the next node after the
                            //package as the place where the import can be added
                            foundPackage = true;
                        }
                    }
                }
            }
            else
            {
                range.endIndex = packageNode.getAbsoluteEnd();
            }
            range.uri = Paths.get(offsetNode.getSourcePath()).toUri().toString();
        }
        return range;
    }
}